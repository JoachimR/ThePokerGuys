package net.thepokerguys.audio

interface AudioFilePlayerEventListener {

    fun onPlaybackChanged(filePath: String, isNowPlaying: Boolean)

    fun onPlayError()

}