package com.kammet.discord.bot

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.doc.standard.CommandInfo
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.json.Json as JsonFeature
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.Permission

@CommandInfo(
    name = ["instagram", "ig"],
    description = "posts instagram pictures and videos from provided link",
)
@RequiredPermissions(Permission.MESSAGE_WRITE)
class InstagramCommand : Command() {

    init {
        name = "instagram"
        aliases = arrayOf("ig")
        help = "posts instagram pictures and videos from provided link"
        arguments = "<link>"
        botPermissions = arrayOf(Permission.MESSAGE_WRITE)
        guildOnly = true
    }

    private val client = HttpClient(Apache) {
        BrowserUserAgent()

        JsonFeature {
            this.serializer = KotlinxSerializer(Json { ignoreUnknownKeys = true })
        }
    }

    override fun execute(event: CommandEvent) {
        val link = event.args.ifEmpty {
            event.replyError("Please provide the instagram link")
            return
        }

        val refactoredLink = instagramLinkHandler(link)

        println(refactoredLink)

        if (refactoredLink == null) {
            event.replyError("Weird link")
            return
        }

        //TODO: private posts

        val posts = runBlocking { instagramHandler(link) }

        when {
            posts.isEmpty() -> event.replyError("There are no posts?")
            else -> for (post in posts) event.reply(post)
        }
    }

    private suspend fun instagramHandler(link: String): List<String> {

        val post: InstagramResponse = client.get(link) {
            headers {
                this["Cookie"] =
                    "ig_cb=2; ig_did=F448025F-8160-411F-8796-CF24E9F05EE1; mid=YBu7dAAEAAEqI7s1NBPEb-rDurOy; shbid=3864; shbts=1620401438.7626996; csrftoken=zycpQfoLpUgYJGxaghdpEjuJU4s93y4C; ds_user_id=1687908138; sessionid=1687908138%3AgYGLqOXMyg9jIq%3A24; datr=VEouYFzDVgiwgakA5W6ziy05; rur=FRC"
            }
        }

        val shortcodeMedia = post.graphql.shortcode_media

        return when (val edgeSidecarToChildren = shortcodeMedia.edge_sidecar_to_children) {
            null -> listOf(shortcodeMedia.url)
            else -> edgeSidecarToChildren.edges.map { it.node.url }
        }
    }

    private fun instagramLinkHandler(link: String): String? = when {
        Regex("(https?://(?:www\\.)?instagram\\.com/p/([^/?#&]+)).*").matches(link) -> link.substringBeforeLast('/') + "/?__a=1"
        else -> null
    }

}