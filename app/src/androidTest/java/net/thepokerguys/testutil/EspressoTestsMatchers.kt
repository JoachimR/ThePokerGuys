package net.thepokerguys.testutil

import android.view.View

import org.hamcrest.Matcher

/**
 * https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f
 */
object EspressoTestsMatchers {

    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun noDrawable(): Matcher<View> {
        return DrawableMatcher(-1)
    }

}