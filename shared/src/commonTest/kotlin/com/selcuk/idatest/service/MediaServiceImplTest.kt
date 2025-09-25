package com.selcuk.idatest.service

import com.selcuk.idatest.data.service.MediaServiceImpl
import com.selcuk.idatest.mocks.MockMediaPlayer
import com.selcuk.idatest.mocks.MockPlaylistRepository
import com.selcuk.idatest.player.PlayerState
import com.selcuk.idatest.utils.createTestPlaylistItem
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MediaServiceImplTest {
    
    private lateinit var mockPlaylistRepository: MockPlaylistRepository
    private lateinit var mockMediaPlayer: MockMediaPlayer
    private lateinit var mediaService: MediaServiceImpl
    
    @BeforeTest
    fun setup() {
        mockPlaylistRepository = MockPlaylistRepository()
        mockMediaPlayer = MockMediaPlayer()
        mediaService = MediaServiceImpl(mockPlaylistRepository, mockMediaPlayer)
    }
    
    @Test
    fun `should get playlists from repository`() = runTest {
        val expectedPlaylists = listOf(
            createTestPlaylistItem(id = "1", title = "Song 1"),
            createTestPlaylistItem(id = "2", title = "Song 2")
        )
        mockPlaylistRepository.setPlaylists(expectedPlaylists)
        
        val result = mediaService.getPlaylist()
        
        assertEquals(expectedPlaylists, result)
        assertTrue(mockPlaylistRepository.getPlaylistCalled)
    }
    
    @Test
    fun `should start playing when item is stopped and togglePlayPause is called`() = runTest {
        val testItem = createTestPlaylistItem(id = "1", title = "Test Song")
        mockMediaPlayer.setState(PlayerState.Idle)
        
        mediaService.togglePlayPause(testItem)
        
        assertTrue(mockMediaPlayer.togglePlayPauseCalled)
        assertEquals(testItem, mockMediaPlayer.lastToggleItem)
        assertTrue(mockMediaPlayer.currentState is PlayerState.Playing)
    }
    
    @Test
    fun `should pause when same item is playing and togglePlayPause is called`() = runTest {
        val testItem = createTestPlaylistItem(id = "1", title = "Test Song")
        mockMediaPlayer.setState(PlayerState.Playing(testItem))
        
        mediaService.togglePlayPause(testItem)
        
        assertTrue(mockMediaPlayer.togglePlayPauseCalled)
        assertEquals(testItem, mockMediaPlayer.lastToggleItem)
        assertTrue(mockMediaPlayer.currentState is PlayerState.Paused)
    }
    
    @Test
    fun `should handle empty playlists`() = runTest {
        mockPlaylistRepository.setPlaylists(emptyList())
        
        val result = mediaService.getPlaylist()
        
        assertTrue(result.isEmpty())
        assertTrue(mockPlaylistRepository.getPlaylistCalled)
    }
    
    @Test
    fun `should delegate state to media player`() = runTest {
        val testItem = createTestPlaylistItem()
        mockMediaPlayer.setState(PlayerState.Playing(testItem))
        
        val currentState = mediaService.state.value

        assertTrue(currentState is PlayerState.Playing)
        assertEquals(testItem.id, currentState.mediaItem.id)
    }
}
