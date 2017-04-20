package net.thepokerguys.testutil

import android.annotation.SuppressLint
import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment
import net.thepokerguys.R
import net.thepokerguys.UnderTestAppActivity
import org.junit.Rule

abstract class FragmentTest<T : Fragment> {

    companion object {

        val FRAGMENT_TAG = "fragment_under_test"

    }

    @get:Rule
    var activityRule = ActivityTestRule(UnderTestAppActivity::class.java)

    val activity: Activity
        get() = activityRule.activity

    var fragment: T? = null

    protected abstract fun onCreateFragment(): T

    @SuppressLint("CommitTransaction")
    protected fun launchFragment() {
        val fragmentManager = activityRule.activity.supportFragmentManager

        runOnUiThreadAndIdleSync(activityRule,
                Runnable {
                    fragment = onCreateFragment()
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.under_test_content_view, fragment, FRAGMENT_TAG)
                            .commitNow()
                })
    }

    fun runOnUiThreadAndIdleSync(rule: ActivityTestRule<*>, runnable: Runnable) {
        try {
            rule.runOnUiThread(runnable)
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        } catch (throwable: Throwable) {
            throw RuntimeException(throwable)
        }

    }

}
