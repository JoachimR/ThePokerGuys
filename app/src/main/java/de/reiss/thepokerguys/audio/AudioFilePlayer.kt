package de.reiss.thepokerguys.audio

interface AudioFilePlayer {

    fun play(filePath: String, progress: Int? = null, duration: Int? = null, speed: Float? = null)

    fun pause(filePath: String)

    fun isPlaying(filePath: String): Boolean

    fun isSetToFile(filePath: String): Boolean

    fun seekTo(position: Int)

    fun seekRelative(toCurrentPosition: Int)

    fun currentProgress(): Int

    fun changeSpeed(speed: Float)

    fun shutdown()

}