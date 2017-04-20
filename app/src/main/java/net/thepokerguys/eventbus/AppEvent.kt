package net.thepokerguys.eventbus

open class AppEvent

class DownloadConfirmed(val url: String) : AppEvent()

class DownloadStarted(val url: String) : AppEvent()

class DownloadStopped(val url: String? = null) : AppEvent()

class Mp3FileProgress(val url: String, val filePath: String, val progressInMs: Int) : AppEvent()

class AudioFilePlaybackChanged(val filePath: String) : AppEvent()

class PlayButtonStateChanged(val isSetToSomeFile: Boolean, val isCurrentlyPlaying: Boolean) : AppEvent()

class FileDeleted(val title: String, val filePath: String) : AppEvent()

class FileRestored() : AppEvent()