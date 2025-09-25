package com.selcuk.idatest.domain.repository

import com.selcuk.idatest.data.model.PlaylistItem

interface PlaylistRepository {
    @Throws(Exception::class)
    suspend fun getPlaylist(): List<PlaylistItem>
}