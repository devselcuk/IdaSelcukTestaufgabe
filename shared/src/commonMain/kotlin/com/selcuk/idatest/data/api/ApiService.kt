package com.selcuk.idatest.data.api

import com.selcuk.idatest.data.model.PlaylistItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

interface ApiService {
    suspend fun getPlaylist(): List<PlaylistItem>
}

class KtorApiService(private val client: HttpClient): ApiService {
    override suspend fun getPlaylist(): List<PlaylistItem> {
        val response = client.get(ApiConfig.BASE_URL + ApiConfig.Endpoints.PLAYLIST)
        println(response.bodyAsText())
        return response.body<List<PlaylistItem>>()
    }
}

