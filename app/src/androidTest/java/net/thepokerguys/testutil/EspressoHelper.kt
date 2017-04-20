package net.thepokerguys.testutil

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.v7.widget.RecyclerView

object EspressoHelper {

    fun onSnackbarText(): ViewInteraction {
        return onView(withId(android.support.design.R.id.snackbar_text))
    }

    fun onRecyclerView(@IdRes recyclerViewResId: Int,
                       itemPosition: Int,
                       @IdRes viewInItem: Int): ViewInteraction {
        onView(withId(recyclerViewResId)).perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(itemPosition))

        return onView(RecyclerViewMatcher.withRecyclerView(recyclerViewResId)
                .atPositionOnView(itemPosition, viewInItem))
    }

}