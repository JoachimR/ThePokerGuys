package net.thepokerguys

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PackageTest {

    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("net.thepokerguys", appContext.packageName)
    }

}
