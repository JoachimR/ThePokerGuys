package net.thepokerguys.util

import android.widget.SeekBar

abstract class OnSeekBarChangedByUserListener : SeekBar.OnSeekBarChangeListener {

    abstract fun changedByUserTo(progress: Int)

    private var hasJustBeenChangedByUser = false
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        hasJustBeenChangedByUser = fromUser
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (hasJustBeenChangedByUser) {
            seekBar?.progress?.let {
                changedByUserTo(it)
            }
            hasJustBeenChangedByUser = false
        }
    }

}