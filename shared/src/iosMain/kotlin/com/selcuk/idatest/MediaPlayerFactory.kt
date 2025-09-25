package com.selcuk.idatest

import com.selcuk.idatest.player.MediaPlayer

actual object MediaPlayerFactory {
    actual fun makeMediaPlayer(): MediaPlayer = IOSMediaPlayer()
}