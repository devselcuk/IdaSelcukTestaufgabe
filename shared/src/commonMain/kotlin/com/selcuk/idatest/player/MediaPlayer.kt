package com.selcuk.idatest.player

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.selcuk.idatest.data.model.PlaylistItem
import kotlinx.coroutines.flow.StateFlow

interface  MediaPlayer {
    @NativeCoroutines
    var state: StateFlow<PlayerState>

    fun togglePlayPause(item: PlaylistItem)
}