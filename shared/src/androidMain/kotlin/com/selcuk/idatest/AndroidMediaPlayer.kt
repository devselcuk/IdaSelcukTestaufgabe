package com.selcuk.idatest

import com.selcuk.idatest.data.model.PlaylistItem
import com.selcuk.idatest.player.MediaPlayer
import com.selcuk.idatest.player.PlayerState
import kotlinx.coroutines.flow.StateFlow

class AndroidMediaPlayer : MediaPlayer {
    override lateinit var state: StateFlow<PlayerState>

    override fun togglePlayPause(item: PlaylistItem) {
        TODO("Not yet implemented")
    }
}