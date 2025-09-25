package com.selcuk.idatest

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.selcuk.idatest.data.model.PlaylistItem
import com.selcuk.idatest.player.MediaPlayer
import com.selcuk.idatest.player.PlayerState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionMode
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.AVAudioSessionModeMoviePlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerTimeControlStatusPaused
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.*
import platform.MediaPlayer.*
import platform.Foundation.NSError

@OptIn(ExperimentalForeignApi::class)
class IOSMediaPlayer : MediaPlayer {
    private val avPlayer = AVPlayer()
    private var currentItem: PlaylistItem? = null  // Track current item

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Stopped)
    @NativeCoroutines
    override var state: StateFlow<PlayerState> = _state

    init {
        setBackgroundPlay()
    }

    override fun togglePlayPause(item: PlaylistItem) {
        if (item.audioUrl == null) {
            _state.value = PlayerState.Error(item)
            return
        }

        val url = NSURL(string = item.audioUrl)

        when (val currentStatus = _state.value) {
            is PlayerState.Playing -> {
                if (currentStatus.mediaItem.id == item.id) {
                    avPlayer.pause()
                    _state.value = PlayerState.Paused(item)
                } else {
                    playMediaUrl(url)
                    _state.value = PlayerState.Playing(item)
                }
            }

            is PlayerState.Paused -> {
                if (currentStatus.mediaItem.id == item.id) {
                    avPlayer.play()
                    _state.value = PlayerState.Playing(item)
                } else {
                    playMediaUrl(url)
                    _state.value = PlayerState.Playing(item)
                }
            }
            else -> {
                playMediaUrl(url)
                _state.value = PlayerState.Playing(item)
            }
        }

        // Track current item and update Now Playing Info
        currentItem = item
        updateNowPlayingInfo(item)
    }

    private fun playMediaUrl(url: NSURL) {
        avPlayer.replaceCurrentItemWithPlayerItem(
            AVPlayerItem(uRL = url)
        )
        avPlayer.play()
    }



    private fun setBackgroundPlay() {
        val session = AVAudioSession.sharedInstance()
        
        session.setCategory(
            category = AVAudioSessionCategoryPlayback,
            error = null
        )
        
        // Activate session
        session.setActive(true, error = null)
        
        setupRemoteCommandCenter()
    }

    private fun updateNowPlayingInfo(item: PlaylistItem) {
        
        val nowPlayingInfo = mutableMapOf<Any?, Any?>()
        
        val title = item.title
        val artist = item.artist
        
        nowPlayingInfo[MPMediaItemPropertyTitle] = title
        nowPlayingInfo[MPMediaItemPropertyArtist] = artist
        
        val isPlaying = _state.value is PlayerState.Playing
        val playbackRate = if (isPlaying) 1.0 else 0.0
        nowPlayingInfo[MPNowPlayingInfoPropertyPlaybackRate] = playbackRate
        
        avPlayer.currentItem?.duration?.let { duration ->
            val durationSeconds = CMTimeGetSeconds(duration)
            if (durationSeconds.isFinite() && !durationSeconds.isNaN() && durationSeconds > 0) {
                nowPlayingInfo[MPMediaItemPropertyPlaybackDuration] = durationSeconds
            }
        }

        val currentTime = CMTimeGetSeconds(avPlayer.currentTime())
        if (currentTime.isFinite() && !currentTime.isNaN() && currentTime >= 0) {
            nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = currentTime
        }
        
        MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = nowPlayingInfo
    }


    
    private fun setupRemoteCommandCenter() {
        val commandCenter = MPRemoteCommandCenter.sharedCommandCenter()
        
        commandCenter.playCommand.enabled = true
        commandCenter.playCommand.addTargetWithHandler { _ ->
            avPlayer.play()
            // Update state when remote play is triggered
            currentItem?.let { item ->
                _state.value = PlayerState.Playing(item)
                updateNowPlayingInfo(item)
            }
            MPRemoteCommandHandlerStatusSuccess
        }

        commandCenter.pauseCommand.enabled = true
        commandCenter.pauseCommand.addTargetWithHandler { _ ->
            avPlayer.pause()
            // Update state when remote pause is triggered
            currentItem?.let { item ->
                _state.value = PlayerState.Paused(item)
                updateNowPlayingInfo(item)
            }
            MPRemoteCommandHandlerStatusSuccess
        }
    }
}