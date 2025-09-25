package com.selcuk.idatest.data.service

import com.selcuk.idatest.data.model.PlaylistItem
import com.selcuk.idatest.domain.repository.PlaylistRepository
import com.selcuk.idatest.domain.service.MediaService
import com.selcuk.idatest.player.MediaPlayer
import com.selcuk.idatest.player.PlayerState
import kotlinx.coroutines.flow.StateFlow

class MediaServiceImpl(
    private val playlistRepository: PlaylistRepository,
    private val mediaPlayer: MediaPlayer
): MediaService {
    override var state: StateFlow<PlayerState> = mediaPlayer.state

    override fun togglePlayPause(item: PlaylistItem) {
        mediaPlayer.togglePlayPause(item)
    }

    override suspend fun getPlaylist(): List<PlaylistItem> {
        return try {
            playlistRepository.getPlaylist()
        } catch (e: Exception) {
            println("Error getting playlist: ${e.message}")
            emptyList()
        }
    }
}