//
//  TimeoutAsyncImage.swift
//  iosApp
//
//  Created by SELCUK YILDIZ on 25.09.25.
//

import SwiftUI

struct TimeoutAsyncImage: View {
    let url: String?
    let timeout: TimeInterval
    let imageSize: CGSize
    
    @State private var isLoading = true
    @State private var hasTimedOut = false
    
    init(url: String?, timeout: TimeInterval = 10.0, imageSize: CGSize) {
        self.url = url
        self.timeout = timeout
        self.imageSize = imageSize
    }
    
    var body: some View {
        AsyncImage(url: URL(string: url ?? "")) { phase in
            switch phase {
            case .empty:
                if hasTimedOut {
                    fallbackImage
                } else {
                    ProgressView()
                        .onAppear {
                            startTimeout()
                        }
                }
            case .success(let image):
                image
                    .resizable()
                    .onAppear {
                        isLoading = false
                    }
            case .failure:
                fallbackImage
            @unknown default:
                fallbackImage
            }
        }
        .frame(width: imageSize.width, height: imageSize.height)
        
    }
    
    private var fallbackImage: some View {
        ZStack {
            Color.gray.opacity(0.4)
            Image(systemName: "music.quarternote.3")
                .symbolVariant(.fill.circle)
            
        }
    }
    
    private func startTimeout() {
        Task {
            try? await Task.sleep(for: .seconds(timeout))
            await MainActor.run {
                if isLoading {
                    hasTimedOut = true
                }
            }
        }
    }
}
