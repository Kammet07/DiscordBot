package com.kammet.discord.bot

import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity

fun main() {
    val dotenv = dotenv()

    val waiter = EventWaiter()
    val client = CommandClientBuilder()
        .setPrefix(dotenv["PREFIX"])
        .setStatus(OnlineStatus.ONLINE)
        .setOwnerId(dotenv["OWNER_ID"])
        .setActivity(Activity.playing("sharing naky content)"))
        .addCommands(InstagramCommand())


    val api = JDABuilder
        .createDefault(dotenv["BOT_TOKEN"])
        .addEventListeners(waiter, client.build())
        .build()
    api.awaitReady()
}
