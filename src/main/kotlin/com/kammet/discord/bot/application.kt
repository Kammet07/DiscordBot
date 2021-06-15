package com.kammet.discord.bot

import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.kammet.discord.bot.com.kammet.discord.bot.commands.InstagramCommand
import com.kammet.discord.bot.com.kammet.discord.bot.commands.TwitterCommand
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import io.ktor.client.features.json.Json as JsonFeature


val dotenv = dotenv()

val client = HttpClient(Apache) {
    BrowserUserAgent()

    JsonFeature {
        this.serializer = KotlinxSerializer(Json { ignoreUnknownKeys = true })
    }
}

fun main() {
    val waiter = EventWaiter()
    val client = CommandClientBuilder()
        .setPrefix(dotenv["PREFIX"])
        .setStatus(OnlineStatus.ONLINE)
        .setOwnerId(dotenv["OWNER_ID"])
        .setActivity(Activity.playing("sharing naky content)"))
        .addCommands(InstagramCommand(), TwitterCommand())


    val api = JDABuilder
        .createDefault(dotenv["BOT_TOKEN"])
        .addEventListeners(waiter, client.build())
        .build()
    api.awaitReady()
}
