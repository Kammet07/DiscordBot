package com.kammet.discord.bot.com.kammet.discord.bot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.kammet.discord.bot.client
import com.kammet.discord.bot.dotenv
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.Permission

class InstagramCommand : Command() {

    init {
        name = "instagram"
        aliases = arrayOf("ig")
        help = "posts instagram pictures and videos from provided link"
        arguments = "<link>"
        botPermissions = arrayOf(Permission.MESSAGE_WRITE)
        guildOnly = true
    }

    override fun execute(event: CommandEvent) {
        val link = event.args.ifEmpty {
            event.replyError("Please provide the instagram link")
            return
        }

        val refactoredInstagramLink = instagramLinkHandler(link)

        if (refactoredInstagramLink == null) {
            event.replyError("Weird link")
            return
        }

        val posts = runBlocking { instagramHandler(refactoredInstagramLink) }

        when {
            posts == null -> event.replyError("Server issue :(")
            posts.isEmpty() -> event.replyError("There are no posts?")
            else -> for (post in posts) event.reply(post)
        }
    }

    private suspend fun instagramHandler(link: String): List<String>? {
        val httpResponse: HttpResponse? = try {
            client.get(link) {
                headers {
                    this["Cookie"] = dotenv["IG_COOKIE"]
                }
            }
        } catch (e: ClientRequestException) {
            return null
        }


        val post: InstagramResponse = httpResponse!!.receive()
        val shortcodeMedia = post.graphql.shortcode_media

        return when (val edgeSidecarToChildren = shortcodeMedia.edge_sidecar_to_children) {
            null -> listOf(shortcodeMedia.url)
            else -> edgeSidecarToChildren.edges.map { it.node.url }
        }
    }

    private fun instagramLinkHandler(link: String): String? =
        when (val result = Regex("(p/|^)([^/]{11})").find(link)?.groupValues?.get(2)) {
            null -> null
            else -> "https://www.instagram.com/p/$result/?__a=1"
        }

}