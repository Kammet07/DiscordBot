package com.kammet.discord.bot

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.dotenv

suspend fun main() {
    val dotenv = dotenv()
    val envVar = dotenv["MATH_SUCKS_BOT_TOKEN"]

    val client = Kord(envVar)
    val pingPong = ReactionEmoji.Custom(Snowflake(527851929968050176), "UTurn", true)

    client.on<MessageCreateEvent> {
        println("${message.author?.username}#${message.author?.discriminator}: ${message.content}")
        when {
            message.content == "!ping" -> {
                val response = message.channel.createMessage("Pong!")
                response.addReaction(pingPong)
            }
            message.content.startsWith("!ig ") -> {
                if (message.content.substring(3).length < 40) message.channel.createMessage("Weird link")
                else {
                    val posts = instagramHandler(message.content.substring(3))

                    if (posts.isEmpty()) message.channel.createMessage("Something went wrong ||Handler error you idiot||")
                    else for (post in posts) message.channel.createMessage(post)
                }
            }
            else -> return@on
        }
    }
    client.login()

}
