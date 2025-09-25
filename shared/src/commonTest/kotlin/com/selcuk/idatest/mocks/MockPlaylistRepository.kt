package com.selcuk.idatest.mocks

import com.selcuk.idatest.data.model.PlaylistItem
import com.selcuk.idatest.domain.repository.PlaylistRepository

class MockPlaylistRepository : PlaylistRepository {
    private var playlists: List<PlaylistItem> = emptyList()
    var getPlaylistCalled = false
    var shouldThrowError = false
    
    fun setPlaylists(playlists: List<PlaylistItem>) {
        this.playlists = playlists
    }
    
    override suspend fun getPlaylist(): List<PlaylistItem> {
        getPlaylistCalled = true
        if (shouldThrowError) {
            throw Exception("Repository error")
        }
        return playlists
    }
}
