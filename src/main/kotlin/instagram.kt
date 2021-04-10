package com.kammet.discord.bot

import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
import java.util.regex.Pattern

@Suppress("UNCHECKED_CAST")
suspend fun instagramHandler(link: String): ArrayList<String> {
    val result = ArrayList<String>()
    val refactoredLink = instagramLinkHandler(link)

    if (refactoredLink != null) {
        val post: String = HttpClient().get(refactoredLink)

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

fun instagramLinkHandler(link: String): String? =
    if (!Pattern.compile("(https?://(?:www\\.)?instagram\\.com/p/([^/?#&]+)).*").matcher(link)
            .matches()
    ) link.substring(0, 41) + "?__a=1" else null


//https://www.instagram.com/p/CNL5FCUJDjk/
//https://www.instagram.com/p/CNPI_GApJiQ/