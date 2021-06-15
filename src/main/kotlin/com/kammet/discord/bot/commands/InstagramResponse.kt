package com.kammet.discord.bot.com.kammet.discord.bot.commands

import kotlinx.serialization.Serializable

@Serializable
data class InstagramResponse(val graphql: Graphql)

@Serializable
data class Graphql(val shortcode_media: ShortcodeMedia)

@Serializable
data class ShortcodeMedia(
    val edge_sidecar_to_children: EdgeSidecarToChildren?,
    val display_url: String? = null,
    val video_url: String? = null
) {
    val url: String get() = video_url ?: display_url!!

}

@Serializable
data class EdgeSidecarToChildren(val edges: Collection<Edge>)

@Serializable
data class Edge(val node: Node)

@Serializable
data class Node(
    val display_url: String? = null,
    val video_url: String? = null
) {
    val url: String get() = video_url ?: display_url!!
}
