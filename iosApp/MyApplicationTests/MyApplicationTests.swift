//
//  MyApplicationTests.swift
//  MyApplicationTests
//
//  Created by SELCUK YILDIZ on 25.09.25.
//

import Testing
import Foundation
@testable import MyApplication
import Shared

@MainActor
struct ContentViewModelTests {
    func createTestPlaylistItem(
        id: String = "test-id",
        title: String = "Test Title",
        artist: String = "Test Artist",
        description: String = "Test Description",
        audioUrl: String = "https://test.com/audio.mp3",
        imageUrl: String = "https://test.com/image.jpg"
    ) -> PlaylistItem {
        return PlaylistItem(
            title: title,
            artist: artist,
            description: description,
            imageUrl: imageUrl,
            audioUrl: audioUrl,
            id: id
        )
    }
    
    // MARK: - Tests
    @Test("ViewModel should initialize with loading state")
    func testInitialState() async throws {
        let mockService = MockMediaService()
        
        let viewModel = ContentViewModel(mediaService: mockService)
        
        #expect(viewModel.state == .loading)
        #expect(viewModel.playerState is PlayerState.Stopped)
    }
    
    @Test("Should load playlist successfully")
    func testGetMediaItemsSuccess() async throws {
        let mockService = MockMediaService()
        let testItems = [
            createTestPlaylistItem(id: "1", title: "Song 1"),
            createTestPlaylistItem(id: "2", title: "Song 2")
        ]
        mockService.getPlaylistResult = testItems
        
        let viewModel = ContentViewModel(mediaService: mockService)
        
        viewModel.getMediaItems()
        
        try await Task.sleep(for: .milliseconds(100))
        
        #expect(mockService.getPlaylistCalled)
        if case .success(let items) = viewModel.state {
            #expect(items.count == 2)
            #expect(items[0].title == "Song 1")
            #expect(items[1].title == "Song 2")
        } else {
            Issue.record("Expected success state but got \(viewModel.state)")
        }
    }
    
    @Test("Should handle empty playlist as failure")
    func testGetMediaItemsEmptyPlaylist() async throws {
        let mockService = MockMediaService()
        mockService.getPlaylistResult = []
        
        let viewModel = ContentViewModel(mediaService: mockService)
        
        viewModel.getMediaItems()
        
        try await Task.sleep(for: .milliseconds(100))
        
        #expect(mockService.getPlaylistCalled)
        #expect(viewModel.state == .failure)
    }
    
    @Test("Should handle API error")
    func testGetMediaItemsAPIError() async throws {
        let mockService = MockMediaService()
        mockService.getPlaylistShouldThrow = true
        
        let viewModel = ContentViewModel(mediaService: mockService)
        
        viewModel.getMediaItems()
        
        try await Task.sleep(for: .milliseconds(100))
        
        #expect(mockService.getPlaylistCalled)
        #expect(viewModel.state == .failure)
    }
    
    @Test("Should timeout after 10 seconds if still loading", .timeLimit(.minutes(1)))
    func testTimeoutHandling() async throws {
        let mockService = MockMediaService()
        mockService.shouldNeverReturn = true
        
        let viewModel = ContentViewModel(mediaService: mockService)
        
        viewModel.getMediaItems()
        
        // Wait for the full 10-second timeout plus a buffer
        try await Task.sleep(for: .seconds(11))
        
        // Then - After 10 seconds, it should timeout and show failure
        #expect(mockService.getPlaylistCalled)
        #expect(viewModel.state == .failure)
    }
    
    @Test("Should toggle play pause when item is tapped")
    func testOnItemTap() async throws {
        let mockService = MockMediaService()
        let testItem = createTestPlaylistItem(id: "1", title: "Test Song")
        
        let viewModel = ContentViewModel(mediaService: mockService)
        
        viewModel.onItemTap(testItem)
        
        #expect(mockService.togglePlayPauseCalled)
        #expect(mockService.lastToggleItem?.id == testItem.id)
    }
}

// MARK: - Mock Classes
class MockMediaService: MediaService {
    // Limitation here for mock service is having Kotlin Flows
    // That's why for a quick solution accessing values from actual service to override them in mock service
    private let actualService = SharedDependencies.shared.mediaService
    
    var getPlaylistCalled = false
    var togglePlayPauseCalled = false
    var togglePlayPauseCallCount = 0
    var lastToggleItem: PlaylistItem?
    var getPlaylistResult: [PlaylistItem] = []
    var getPlaylistShouldThrow = false
    var shouldNeverReturn = false
    var responseDelay: TimeInterval = 0.0
    
    // Also looks weird in a real app this should be handled in a better way
    // Result of mocking kotlin interfaces in Swift tests
    var state: ((@escaping (PlayerState, @escaping () -> KotlinUnit, KotlinUnit) -> KotlinUnit, @escaping ((any Error)?, KotlinUnit) -> KotlinUnit, @escaping (any Error, KotlinUnit) -> KotlinUnit) -> () -> KotlinUnit) {
        return actualService.state
    }
    
    var stateValue: PlayerState {
        return actualService.stateValue
    }
    
    func getPlaylist() async throws -> [PlaylistItem] {
        getPlaylistCalled = true
        
        if shouldNeverReturn {
            try await Task.sleep(for: .seconds(15))
        }
        
        if responseDelay > 0 {
            try await Task.sleep(for: .seconds(responseDelay))
        }
        
        if getPlaylistShouldThrow {
            throw NSError(domain: "MockError", code: 1, userInfo: [NSLocalizedDescriptionKey: "Mock network error"])
        }
        
        return getPlaylistResult
    }
    
    func togglePlayPause(item: PlaylistItem) {
        togglePlayPauseCalled = true
        togglePlayPauseCallCount += 1
        lastToggleItem = item
    }
}
