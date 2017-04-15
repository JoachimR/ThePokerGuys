package de.reiss.thepokerguys.audio

interface AudioFilePlayerEventListener {

    fun onPlaybackChanged(filePath: String, isNowPlaying: Boolean)

    fun onPlayError()

}