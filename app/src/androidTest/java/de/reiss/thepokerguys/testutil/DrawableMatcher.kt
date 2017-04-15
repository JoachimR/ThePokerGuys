package de.reiss.thepokerguys.testutil

import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f
 */
class DrawableMatcher(private val expectedId: Int) : TypeSafeMatcher<View>(View::class.java) {

    internal var resourceName: String? = null

    override fun matchesSafely(target: View): Boolean {
        if (target !is ImageView) {
            return false
        }
        val imageView = target
        if (expectedId < 0) {
            return imageView.drawable == null
        }
        val resources = target.getContext().resources
        @Suppress("DEPRECATION")
        val expectedDrawable = resources.getDrawable(expectedId)
        resourceName = resources.getResourceEntryName(expectedId)

        if (expectedDrawable == null) {
            return false
        }

        val actualBitmap = (imageView.drawable as BitmapDrawable).bitmap
        val expectedBitmap = (expectedDrawable as BitmapDrawable).bitmap

        return actualBitmap.sameAs(expectedBitmap)
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable from resource id: ")
        description.appendValue(expectedId)
        if (resourceName != null) {
            description.appendText("[")
            description.appendText(resourceName)
            description.appendText("]")
        }
    }

}