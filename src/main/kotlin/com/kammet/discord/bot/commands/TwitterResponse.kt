package com.kammet.discord.bot.com.kammet.discord.bot.commands

import kotlinx.serialization.Serializable

@Serializable
data class TwitterResponse(val includes: Includes? = null, val errors: Collection<Error>? = null)

@Serializable
data class Includes(val media: Collection<Media>)

@Serializable
data class Media(val url: String? = null) {
    val link: String get() = url ?: "Video yet not available :'("
}

@Serializable
data class Error(val detail: String)