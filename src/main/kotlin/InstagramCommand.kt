package com.kammet.discord.bot

import com.google.gson.Gson
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.doc.standard.CommandInfo
import com.jagrosh.jdautilities.doc.standard.RequiredPermissions
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
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
    suspend fun instagramHandler(link: String): ArrayList<String> {
        val result = ArrayList<String>()
        val refactoredLink = instagramLinkHandler(link)

        if (refactoredLink != null) {
            val post: String = HttpClient(Apache).get(refactoredLink)

            val map: Map<String, *> = Gson().fromJson(post, Map::class.java) as Map<String, *>
            val graphql = map["graphql"] as Map<String, *>
            val shortcodeMedia = graphql["shortcode_media"] as Map<String, *>
            val edgeSidecarToChildren = shortcodeMedia["edge_sidecar_to_children"] as Map<String, *>?

            if (edgeSidecarToChildren == null) result.add(if (shortcodeMedia["video_url"] == null) shortcodeMedia["display_url"].toString() else shortcodeMedia["video_url"].toString())
            else {
                val edges = edgeSidecarToChildren["edges"] as ArrayList<Map<String, *>>

                for (edge in edges) {
                    val node = edge["node"] as Map<String, *>
                    val url = if (node["video_url"] == null) node["display_url"] else node["video_url"]
                    result.add(url.toString())
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