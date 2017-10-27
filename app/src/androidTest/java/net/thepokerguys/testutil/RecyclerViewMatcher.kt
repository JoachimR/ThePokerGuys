package net.thepokerguys.testutil

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * https://github.com/dannyroa/espresso-samples/blob/master/RecyclerView/app/src/androidTest/java/com/dannyroa/espresso_samples/recyclerview/RecyclerViewMatcher.java
 */
class RecyclerViewMatcher(private val recyclerViewId: Int) {

    companion object {

        fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
            return RecyclerViewMatcher(recyclerViewId)
        }

    }

    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {

            internal var resources: Resources? = null

            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (this.resources != null) {
                    try {
                        idDescription = this.resources!!.getResourceName(recyclerViewId)
                    } catch (e: Resources.NotFoundException) {
                        idDescription = String.format("%s (resource name not found)", recyclerViewId)
                    }

                }
                description.appendText("with id: $idDescription")
            }

            public override fun matchesSafely(view: View): Boolean {

                this.resources = view.resources

                var childView: View? = null
                val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId)
                if (recyclerView.id == recyclerViewId) {
                    childView = recyclerView.layoutManager.findViewByPosition(position)
                }

                if (childView == null) {
                    return false
                }

                if (targetViewId == -1) {
                    return view === childView
                } else {
                    val targetView = childView.findViewById<View>(targetViewId)
                    return view === targetView
                }
            }

        }
    }

}
