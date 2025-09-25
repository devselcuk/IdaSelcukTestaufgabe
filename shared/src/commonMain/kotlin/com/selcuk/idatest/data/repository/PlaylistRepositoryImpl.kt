package com.selcuk.idatest.data.repository

import com.selcuk.idatest.data.api.ApiService
import com.selcuk.idatest.data.model.PlaylistItem
import com.selcuk.idatest.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    private val apiService: ApiService
): PlaylistRepository {
    @Throws(Exception::class)
    override suspend fun getPlaylist(): List<PlaylistItem> {
        return apiService.getPlaylist()
    }
}