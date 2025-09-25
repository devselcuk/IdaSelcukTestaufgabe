package com.selcuk.idatest.utils

import com.selcuk.idatest.data.model.PlaylistItem

fun createTestPlaylistItem(
    id: String = "test-id",
    title: String = "Test Title",
    artist: String = "Test Artist",
    description: String = "Test Description",
    audioUrl: String = "https://test.com/audio.mp3",
    imageUrl: String = "https://test.com/image.jpg"
): PlaylistItem {
    return PlaylistItem(
        title = title,
        artist = artist,
        description = description,
        imageUrl = imageUrl,
        audioUrl = audioUrl,
        id = id
    )
}
