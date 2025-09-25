package com.selcuk.idatest.repository

import com.selcuk.idatest.data.repository.PlaylistRepositoryImpl
import com.selcuk.idatest.mocks.MockApiService
import com.selcuk.idatest.utils.createTestPlaylistItem
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaylistRepositoryTest {
    
    private lateinit var mockApiService: MockApiService
    private lateinit var repository: PlaylistRepositoryImpl
    
    @BeforeTest
    fun setup() {
        mockApiService = MockApiService()
        repository = PlaylistRepositoryImpl(mockApiService)
    }
    
    @Test
    fun `should return playlists from API service`() = runTest {
        val expectedPlaylists = listOf(
            createTestPlaylistItem("1", "Song 1", "Artist 1", "url1", "image1"),
            createTestPlaylistItem("2", "Song 2", "Artist 2", "url2", "image2")
        )
        mockApiService.setPlaylists(expectedPlaylists)

        val result = repository.getPlaylist()

        assertEquals(expectedPlaylists, result)
        assertTrue(mockApiService.getPlaylistCalled)
    }
    
    @Test
    fun `should return specific playlist by id`() = runTest {
        val playlists = listOf(
            createTestPlaylistItem("1", "Song 1", "Artist 1", "url1", "image1"),
            createTestPlaylistItem("2", "Song 2", "Artist 2", "url2", "image2")
        )
        mockApiService.setPlaylists(playlists)

        val allPlaylists = repository.getPlaylist()
        val result = allPlaylists.find { it.id == "2" }

        assertEquals("Song 2", result?.title)
        assertEquals("2", result?.id)
        assertEquals("Artist 2", result?.artist)
    }
    
    @Test
    fun `should return null for non-existent playlist id`() = runTest {
        val playlists = listOf(
            createTestPlaylistItem("1", "Song 1", "Artist 1", "url1", "image1")
        )
        mockApiService.setPlaylists(playlists)
        
        val allPlaylists = repository.getPlaylist()
        val result = allPlaylists.find { it.id == "999" }
        
        assertNull(result)
    }
    
    @Test
    fun `should handle empty playlist from API`() = runTest {
        mockApiService.setPlaylists(emptyList())
        
        val result = repository.getPlaylist()
        
        assertTrue(result.isEmpty())
        assertTrue(mockApiService.getPlaylistCalled)
    }
    
    @Test
    fun `should return first matching item when multiple items have same id`() = runTest {
        val playlists = listOf(
            createTestPlaylistItem("1", "First Song", "Artist 1", "url1", "image1"),
            createTestPlaylistItem("1", "Second Song", "Artist 2", "url2", "image2") // Same ID
        )
        mockApiService.setPlaylists(playlists)
        
        val allPlaylists = repository.getPlaylist()
        val result = allPlaylists.find { it.id == "1" }
        
        assertEquals("First Song", result?.title)
        assertEquals("Artist 1", result?.artist)
    }
}
