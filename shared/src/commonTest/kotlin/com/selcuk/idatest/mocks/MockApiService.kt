package com.selcuk.idatest.mocks

import com.selcuk.idatest.data.api.ApiService
import com.selcuk.idatest.data.model.PlaylistItem

class MockApiService : ApiService {
    private var playlists: List<PlaylistItem> = emptyList()
    var getPlaylistCalled = false
    var shouldThrowError = false
    
    fun setPlaylists(playlists: List<PlaylistItem>) {
        this.playlists = playlists
    }
    
    override suspend fun getPlaylist(): List<PlaylistItem> {
        getPlaylistCalled = true
        if (shouldThrowError) {
            throw Exception("API Error")
        }
        return playlists
    }
}
