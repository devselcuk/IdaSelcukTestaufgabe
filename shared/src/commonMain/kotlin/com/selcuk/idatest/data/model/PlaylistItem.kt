package com.selcuk.idatest.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistItem(
    val title : String,
    val artist: String,
    val description: String,
    val imageUrl: String?,
    val audioUrl: String?,
    val id: String
)