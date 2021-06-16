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

class TwitterCommand : Command() {
    init {
        name = "twitter"
        aliases = arrayOf("twt", "twit")
        help = "posts twitter pictures and videos from provided link"
        arguments = "<link>"
        botPermissions = arrayOf(Permission.MESSAGE_WRITE)
        guildOnly = true
    }

    override fun execute(event: CommandEvent) {
        val link = event.args.ifEmpty {
            event.replyError("Please provide the twitter link")
            return
        }

        val refactoredTwitterLink = twitterLinkHandler(link)

        if (refactoredTwitterLink == null) {
            event.replyError("Bad link")
            return
        }

        val posts = runBlocking { twitterHandler(refactoredTwitterLink) }
        println(posts)

        when {
            posts == null -> event.replyError("Server issue :(")
            posts.isEmpty() -> event.replyError("Post has no media attached")
            else -> for (post in posts) event.reply(post)
        }

    }

    private suspend fun twitterHandler(link: String): List<String>? {
        val httpResponse: HttpResponse? = try {
            client.get(link) {
                headers {
                    this["Authorization"] = "Bearer " + dotenv["TWT_BEARER_TOKEN"]
                }
            }
        } catch (e: ClientRequestException) {
            return null
        }

        val post: TwitterResponse = httpResponse!!.receive()

        if (post.errors != null) return post.errors.map { it.detail }
        return post.includes!!.media.map { it.link }
    }

    private fun twitterLinkHandler(link: String): String? =
        when (val result = Regex("(\\d{19})").find(link)?.value) {
            null -> null
            else -> "https://api.twitter.com/2/tweets?ids=$result&expansions=attachments.media_keys&media.fields=url"
        }
}