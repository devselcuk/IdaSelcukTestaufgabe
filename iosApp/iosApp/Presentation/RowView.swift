//
//  RowView.swift
//  iosApp
//
//  Created by SELCUK YILDIZ on 25.09.25.
//

import SwiftUI
import Shared

struct RowView: View {
    let item: PlaylistItem
    let shouldShowOverlay: Bool
    let overlaySystemImageName: String?
    private let imageSize: CGSize = .init(width: 60, height: 60)
    private let symbolSize: CGFloat = 24
    var body: some View {
        HStack(spacing: 16) {
            ZStack {
                TimeoutAsyncImage(url: item.imageUrl,
                                  timeout: 5,
                                  imageSize: imageSize)
 
                if shouldShowOverlay, let imageName = overlaySystemImageName {
                    ZStack {
                        Image(systemName: imageName)
                            .font(.system(size: symbolSize))
                            .transition(.symbolEffect)
                            .frame(width: imageSize.width, height: imageSize.height)
                            .background(.ultraThinMaterial.opacity(0.7))
                    }
                    
                }
            }
            .clipShape(Circle())
            VStack(alignment: .leading) {
                Text(item.artist)
                    .font(Font.headline)
                
                Text(item.description_)
                    .font(.subheadline)
            }
            .multilineTextAlignment(.leading)
            Spacer()
        }
        
        .padding()
    }
}
