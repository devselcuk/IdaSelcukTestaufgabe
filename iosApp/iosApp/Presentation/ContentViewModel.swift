//
//  ContentViewModel.swift
//  iosApp
//
//  Created by SELCUK YILDIZ on 25.09.25.
//

import Observation
import AVFoundation
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesAsync
import Shared

@Observable
class ContentViewModel {
    enum State: Equatable {
        case loading
        case success([PlaylistItem])
        case failure
    }
    
    @ObservationIgnored
    private let mediaService: MediaService
    
    var state: State = .loading
    var playerState: PlayerState = PlayerState.Idle()
    
    init(mediaService: MediaService = SharedDependencies.shared.mediaService) {
        self.mediaService = mediaService
        Task { @MainActor in
            for try await state in asyncSequence(for: mediaService.state) {
                self.playerState = state
            }
        }
    }

    func getMediaItems() {
        startTimeoutCheck()
        Task { @MainActor in
            do {
                state = .loading
                let playlist = try await mediaService.getPlaylist()
                if playlist.isEmpty {
                    self.state = .failure
                } else {
                    state = .success(playlist)
                }
                
            } catch {
                state = .failure
            }
        }
    }
    
    func onItemTap(_ item: PlaylistItem) {
        mediaService.togglePlayPause(item: item)
    }
    
    func playbackInfo(for item: PlaylistItem) -> PlaybackInfo {
        switch playerState {
        case let paused as PlayerState.Paused where paused.mediaItem == item:
            return PlaybackInfo(shouldShow: true, systemImageName: "play.fill")
        case let playing as PlayerState.Playing where playing.mediaItem == item:
            return PlaybackInfo(shouldShow: true, systemImageName: "pause.fill")
        case let error as PlayerState.Error where error.mediaItem == item:
            return PlaybackInfo(shouldShow: true, systemImageName: "exclamationmark.triangle.fill")
        default:
            return PlaybackInfo(shouldShow: false, systemImageName: nil)
        }
    }
    
    private func startTimeoutCheck() {
        Task { @MainActor in
            try await Task.sleep(for: .seconds(10))
            if state == .loading {
                state = .failure
            }
        }
    }
}

struct PlaybackInfo {
    let shouldShow: Bool
    let systemImageName: String?
}
