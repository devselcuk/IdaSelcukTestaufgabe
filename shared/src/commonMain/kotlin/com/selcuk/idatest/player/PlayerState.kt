package com.selcuk.idatest.player

import com.selcuk.idatest.data.model.PlaylistItem

sealed class PlayerState {
    data class Playing(val mediaItem: PlaylistItem) : PlayerState()
    data class Paused(val mediaItem: PlaylistItem) : PlayerState()
    data class Error(val mediaItem: PlaylistItem) : PlayerState()
    data object Stopped : PlayerState()
}