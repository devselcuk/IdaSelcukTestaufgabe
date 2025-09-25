package com.selcuk.idatest.domain.service

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.selcuk.idatest.data.model.PlaylistItem
import com.selcuk.idatest.player.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface MediaService {
    @NativeCoroutines
    var state: StateFlow<PlayerState>

    fun togglePlayPause(item: PlaylistItem)

    @Throws(Exception::class)
    suspend fun getPlaylist(): List<PlaylistItem>
}