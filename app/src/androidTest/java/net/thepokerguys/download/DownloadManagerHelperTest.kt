package net.thepokerguys.download

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.webkit.URLUtil
import net.thepokerguys.testutil.ForAllPodcastsTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadManagerHelperTest : ForAllPodcastsTest() {

    private var downloadManagerHelper =
            DownloadManagerHelper(InstrumentationRegistry.getTargetContext())

    @Test
    fun checkMp3DownloadUrls() {

        assertTrue(DownloadFolder.getDownloadDir().exists())

        testForAllPodcasts {
            assertTrue(URLUtil.isValidUrl(it.url))
            assertNotNull(downloadManagerHelper.createDownloadManagerRequest(it.url))
        }
    }

}