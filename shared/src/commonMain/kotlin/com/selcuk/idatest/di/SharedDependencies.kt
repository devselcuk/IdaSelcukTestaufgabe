package com.selcuk.idatest.di

import com.selcuk.idatest.MediaPlayerFactory
import com.selcuk.idatest.data.api.ApiService
import com.selcuk.idatest.data.api.HttpClientFactory
import com.selcuk.idatest.data.api.KtorApiService
import com.selcuk.idatest.data.repository.PlaylistRepositoryImpl
import com.selcuk.idatest.data.service.MediaServiceImpl
import com.selcuk.idatest.domain.repository.PlaylistRepository
import com.selcuk.idatest.domain.service.MediaService
import com.selcuk.idatest.player.MediaPlayer

object SharedDependencies {
    private val httpClient by lazy { HttpClientFactory.create() }
    private val apiService: ApiService by lazy { KtorApiService(httpClient) }
    private val playlistRepository: PlaylistRepository by lazy { PlaylistRepositoryImpl(apiService) }
    private val player: MediaPlayer by lazy { MediaPlayerFactory.makeMediaPlayer() }

    val mediaService: MediaService by lazy { MediaServiceImpl(playlistRepository, player) }
}