package com.selcuk.idatest.mocks

import com.selcuk.idatest.data.model.PlaylistItem
import com.selcuk.idatest.player.MediaPlayer
import com.selcuk.idatest.player.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MockMediaItem(
    val id: String,
    val title: String,
    val artist: String,
    val audioUrl: String,
    val imageUrl: String?
) {
    fun toPlaylistItem() = PlaylistItem(
        title = title,
        artist = artist,
        description = "Test Description",
        imageUrl = imageUrl,
        audioUrl = audioUrl,
        id = id
    )
}

class MockMediaPlayer : MediaPlayer {
    private val _state = MutableStateFlow<PlayerState>(PlayerState.Stopped)
    override var state: StateFlow<PlayerState> = _state
    
    var togglePlayPauseCalled = false
    var lastToggleItem: PlaylistItem? = null
    
    private var currentItem: PlaylistItem? = null
    val currentState get() = _state.value
    
    override fun togglePlayPause(item: PlaylistItem) {
        togglePlayPauseCalled = true
        lastToggleItem = item
        
        when (val currentStatus = _state.value) {
            is PlayerState.Playing -> {
                if (currentStatus.mediaItem.id == item.id) {
                    _state.value = PlayerState.Paused(item)
                } else {
                    _state.value = PlayerState.Playing(item)
                }
            }
            is PlayerState.Paused -> {
                if (currentStatus.mediaItem.id == item.id) {
                    _state.value = PlayerState.Playing(item)
                } else {
                    _state.value = PlayerState.Playing(item)
                }
            }
            else -> {
                _state.value = PlayerState.Playing(item)
            }
        }
        currentItem = item
    }
    
    fun setState(state: PlayerState) {
        _state.value = state
        if (state is PlayerState.Playing) {
            currentItem = state.mediaItem
        } else if (state is PlayerState.Paused) {
            currentItem = state.mediaItem
        }
    }
    
    fun reset() {
        togglePlayPauseCalled = false
        lastToggleItem = null
        currentItem = null
        _state.value = PlayerState.Stopped
    }
}
