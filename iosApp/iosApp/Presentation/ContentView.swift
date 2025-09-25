import SwiftUI
import Shared

struct ContentView: View {
    @State private var showContent = false
    @State private var viewModel = ContentViewModel()
    
    var loadingView: some View {
        ProgressView()
    }
    
    var errorView: some View {
        VStack {
            Text("Failed to load data")
            Button("Retry") {
                viewModel.getMediaItems()
            }
        }
    }
    
    var body: some View {
        NavigationStack {
            Group {
                switch viewModel.state {
                case .loading:
                    loadingView
                case .failure:
                    errorView
                case .success(let playlist):
                    makeSuccessView(with: playlist)
                }
            }
            .navigationTitle("Playlist")
            .onAppear {
                viewModel.getMediaItems()
            }
        }
    }
    
    @ViewBuilder
    func makeSuccessView(with playlist: [PlaylistItem]) -> some View {
        ScrollView {
            LazyVStack {
                ForEach(playlist, id: \.self) { item in
                    RowView(item: item,
                            shouldShowOverlay: playbackInfo(for: item).shouldShow,
                            overlaySystemImageName: playbackInfo(for: item).systemImageName)
                    .environment(viewModel)
                    .onTapGesture {
                        viewModel.onItemTap(item)
                    }
                    Divider()
                }
            }
        }
    }
    
    func playbackInfo(for item: PlaylistItem) -> PlaybackInfo {
        viewModel.playbackInfo(for: item)
    }
}

#Preview {
    ContentView()
}




