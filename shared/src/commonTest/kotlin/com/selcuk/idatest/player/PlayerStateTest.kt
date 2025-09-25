package com.selcuk.idatest.player

import com.selcuk.idatest.utils.createTestPlaylistItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlayerStateTest {
    
    @Test
    fun `stopped state should be correct type`() {
        val state = PlayerState.Stopped
        
        assertTrue(state is PlayerState.Stopped)
        assertFalse(state is PlayerState.Playing)
        assertFalse(state is PlayerState.Paused)
        assertFalse(state is PlayerState.Error)
    }
    
    @Test
    fun `playing state should contain correct item`() {
        val item = createTestPlaylistItem("1", "Test Song", "Test Artist", "url", "image")
        val state = PlayerState.Playing(item)
        
        assertTrue(state is PlayerState.Playing)
        assertEquals(item, state.mediaItem)
        assertEquals("Test Song", state.mediaItem.title)
        assertEquals("Test Artist", state.mediaItem.artist)
        assertEquals("1", state.mediaItem.id)
    }
    
    @Test
    fun `paused state should contain correct item`() {
        val item = createTestPlaylistItem("2", "Paused Song", "Paused Artist", "url2", "image2")
        val state = PlayerState.Paused(item)
        
        assertTrue(state is PlayerState.Paused)
        assertEquals(item, state.mediaItem)
        assertEquals("Paused Song", state.mediaItem.title)
        assertEquals("Paused Artist", state.mediaItem.artist)
        assertEquals("2", state.mediaItem.id)
    }
    
    @Test
    fun `error state should contain correct item`() {
        val item = createTestPlaylistItem(
            id = "3", 
            title = "Error Song", 
            artist = "Error Artist", 
            description = "Error Description",
            audioUrl = "bad-url", 
            imageUrl = "image3"
        )
        val state = PlayerState.Error(item)
        
        assertTrue(state is PlayerState.Error)
        assertEquals(item, state.mediaItem)
        assertEquals("Error Song", state.mediaItem.title)
        assertEquals("bad-url", state.mediaItem.audioUrl)
    }
    
    @Test
    fun `different states with same item should not be equal`() {
        val item = createTestPlaylistItem("1", "Test Song", "Test Artist", "url", "image")
        val playingState = PlayerState.Playing(item)
        val pausedState = PlayerState.Paused(item)
        val errorState = PlayerState.Error(item)
        
        assertFalse(playingState == pausedState)
        assertFalse(playingState == errorState)
        assertFalse(pausedState == errorState)
        assertFalse(playingState == PlayerState.Stopped)
    }
    
    @Test
    fun `same states with same item should be equal`() {
        val item1 = createTestPlaylistItem("1", "Test Song", "Test Artist", "url", "image")
        val item2 = createTestPlaylistItem("1", "Test Song", "Test Artist", "url", "image")
        val state1 = PlayerState.Playing(item1)
        val state2 = PlayerState.Playing(item2)
        
        assertEquals(state1, state2)
    }
    
    @Test
    fun `states with different items should not be equal`() {
        val item1 = createTestPlaylistItem("1", "Song 1", "Artist 1", "url1", "image1")
        val item2 = createTestPlaylistItem("2", "Song 2", "Artist 2", "url2", "image2")
        val state1 = PlayerState.Playing(item1)
        val state2 = PlayerState.Playing(item2)
        
        assertFalse(state1 == state2)
    }
}
