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
import io.ktor.util.date.*
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

        val posts = runBlocking { instagramHandler(refactoredLink) }

        when {
            posts.isEmpty() -> event.replyError("There are no posts?")
            else -> for (post in posts) event.reply(post)
        }
    }

    private suspend fun instagramHandler(link: String): List<String> {
        println(link)

        val post: InstagramResponse = client.get(link) {
            headers {
                this["Cookie"] = dotenv["IG_COOKIE"]
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