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
    override fun execute(event: CommandEvent) {
        val link = event.args.ifEmpty {
            event.replyError("Please provide the instagram link")
            return
        }

        if (link.length < 40) {
            event.replyError("Weird link")
            return
        }

        val posts = runBlocking { instagramHandler(link) }
        if (posts.isEmpty()) event.reply("Something went wrong")
        else for (post in posts) event.reply(post)
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun instagramHandler(link: String): List<String> {
        val result = mutableListOf<String>()
        val refactoredLink = instagramLinkHandler(link)

        println(refactoredLink)
        if (refactoredLink != null) {
            val client = HttpClient(Apache) {
                BrowserUserAgent()

                JsonFeature {
                    this.serializer = KotlinxSerializer(Json { ignoreUnknownKeys = true })
                }
            }

            val post: InstagramResponse = client.get("https://www.instagram.com/p/COmhaGCs-cK/?__a=1") {
                headers {
                    this["Cookie"] =
                        "ig_cb=2; ig_did=F448025F-8160-411F-8796-CF24E9F05EE1; mid=YBu7dAAEAAEqI7s1NBPEb-rDurOy; shbid=3864; shbts=1620401438.7626996; csrftoken=zycpQfoLpUgYJGxaghdpEjuJU4s93y4C; ds_user_id=1687908138; sessionid=1687908138%3AgYGLqOXMyg9jIq%3A24; datr=VEouYFzDVgiwgakA5W6ziy05; rur=FRC"
                }
            }

            val shortcodeMedia = post.graphql.shortcode_media
            val edgeSidecarToChildren = shortcodeMedia.edge_sidecar_to_children

            if (edgeSidecarToChildren == null) {
                result.add(shortcodeMedia.url)
            } else {
                for (edge in edgeSidecarToChildren.edges) {
                    result.add(edge.node.url)
                }
            }
        }

        return result
    }

    private fun instagramLinkHandler(link: String): String? =
        if (Regex("(https?://(?:www\\.)?instagram\\.com/p/([^/?#&]+)).*").matches(link)
        ) link.substring(0, 40) + "?__a=1" else null

    init {
        name = "instagram"
        aliases = arrayOf("ig")
        help = "posts instagram pictures and videos from provided link"
        arguments = "<link>"
        botPermissions = arrayOf(Permission.MESSAGE_WRITE)
        guildOnly = true
    }
}