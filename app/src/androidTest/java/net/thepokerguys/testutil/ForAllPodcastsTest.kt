package net.thepokerguys.testutil

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import net.thepokerguys.database.DbProxy
import net.thepokerguys.database.PodcastDatabaseItem
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class ForAllPodcastsTest {

    lateinit var dbProxy: DbProxy

    @Before
    fun setUp() {
        dbProxy = DbProxy(InstrumentationRegistry.getTargetContext())
    }

    fun testForAllPodcasts(assertionsForPodcast: (podcast: PodcastDatabaseItem) -> Unit) {
        dbProxy.getAllPodcastDatabaseItems().forEach {
            assertionsForPodcast(it)
        }
    }

}