package net.thepokerguys.testutil

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import net.thepokerguys.App
import java.io.InputStream
import java.util.*

val DOUBLE_DELTA = 0.0000000001

fun runOnUiThreadAndIdleSync(rule: ActivityTestRule<*>, runnable: Runnable) {
    try {
        rule.runOnUiThread(runnable)
        waitForIdleSync()
    } catch (throwable: Throwable) {
        throw RuntimeException(throwable)
    }

}

fun waitForIdleSync() {
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
}

fun readResource(resourceName: String): String {
    val scanner = Scanner(streamResource(resourceName), "UTF-8")
    return scanner.useDelimiter("\\A").next()
}

private fun streamResource(resourceName: String): InputStream {
    return App::class.java.getResourceAsStream(resourceName)
}
