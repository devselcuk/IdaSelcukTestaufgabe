package com.selcuk.idatest

import com.selcuk.idatest.player.MediaPlayer

expect object MediaPlayerFactory {
    fun makeMediaPlayer(): MediaPlayer
}

