package de.reiss.thepokerguys.util.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ProgressBar

class TestableProgressBar : ProgressBar {

    companion object {

        var isTestRunning = false

    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setIndeterminateDrawable(drawable: Drawable?) {
        super.setIndeterminateDrawable(if (isTestRunning) null else drawable)
    }

}
