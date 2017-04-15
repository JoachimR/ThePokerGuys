package de.reiss.thepokerguys.list

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.swipeDown
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import de.reiss.thepokerguys.R
import de.reiss.thepokerguys.TestApp
import de.reiss.thepokerguys.database.DbProxy
import de.reiss.thepokerguys.database.PodcastDatabaseItem
import de.reiss.thepokerguys.sampleItem
import de.reiss.thepokerguys.testutil.EspressoHelper.onRecyclerView
import de.reiss.thepokerguys.testutil.EspressoHelper.onSnackbarText
import de.reiss.thepokerguys.testutil.FragmentTest
import de.reiss.thepokerguys.util.emptyMutableList
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rx.Observable
import java.util.*

@RunWith(AndroidJUnit4::class)
class PodcastListFragmentTest : FragmentTest<PodcastListFragment>() {

    private val expectedTitlePrefix = "expected title #"

    private lateinit var dbProxy: DbProxy
    private lateinit var rssDownloader: RssDownloader

    override fun onCreateFragment(): PodcastListFragment {
        return PodcastListFragment()
    }

    @Before
    fun setUp() {
        dbProxy = testApp().defaultMockDbProxy()
        rssDownloader = testApp().defaultMockRssDownloader()

        mockRssDownload(success = true, resultList = emptyMutableList())
        mockDatabaseLoad(success = true, resultList = emptyMutableList())

        launchFragment()
    }

    @Test
    fun downloadFail() {
        mockRssDownload(success = false, resultList = emptyMutableList())

        onView(withId(R.id.podcast_list_fragment_swipe_to_refresh)).perform(swipeDown())

        onSnackbarText().check(matches(withText(R.string.podcast_detail_error_downloading)))
    }

    @Test
    fun downloadSuccessEmpty() {
        val result = expectedItemList(0)
        mockRssDownload(success = true, resultList = result)
        mockDatabaseLoad(success = true, resultList = result)

        onView(withId(R.id.podcast_list_fragment_swipe_to_refresh)).perform(swipeDown())

        onView(withId(R.id.podcast_list_fragment_recycler_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.podcast_list_fragment_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun downloadSuccessOneItem() {
        val result = expectedItemList(1)
        mockRssDownload(success = true, resultList = result)
        mockDatabaseLoad(success = true, resultList = result)

        onView(withId(R.id.podcast_list_fragment_swipe_to_refresh)).perform(swipeDown())

        onView(withId(R.id.podcast_list_fragment_recycler_view)).check(matches(isDisplayed()))
        checkTitleAtPosition(0)
        onView(withId(R.id.podcast_list_fragment_empty)).check(matches(not(isDisplayed())))
    }

    @Test
    fun downloadSuccessTwoItems() {
        val result = expectedItemList(2)
        mockRssDownload(success = true, resultList = result)
        mockDatabaseLoad(success = true, resultList = result)

        onView(withId(R.id.podcast_list_fragment_swipe_to_refresh)).perform(swipeDown())

        onView(withId(R.id.podcast_list_fragment_recycler_view)).check(matches(isDisplayed()))
        checkTitleAtPosition(0)
        checkTitleAtPosition(1)
        onView(withId(R.id.podcast_list_fragment_empty)).check(matches(not(isDisplayed())))
    }

    @Test
    fun downloadSuccess999Item() {
        val result = expectedItemList(999)

        mockRssDownload(success = true, resultList = result)
        mockDatabaseLoad(success = true, resultList = result)

        onView(withId(R.id.podcast_list_fragment_swipe_to_refresh)).perform(swipeDown())

        onView(withId(R.id.podcast_list_fragment_recycler_view)).check(matches(isDisplayed()))
        checkTitleAtPosition(0)
        checkTitleAtPosition(500)
        checkTitleAtPosition(998)
        onView(withId(R.id.podcast_list_fragment_empty)).check(matches(not(isDisplayed())))
    }

    private fun checkTitleAtPosition(position: Int) {
        onRecyclerViewItem(position = position, viewInItem = R.id.podcast_list_item_title)
                .check(matches(withText("$expectedTitlePrefix${position + 1}")))
    }

    private fun mockRssDownload(success: Boolean,
                                resultList: MutableList<PodcastDatabaseItem>) {
        if (success) {
            rssDownloader = mock {
                on { execute() } doReturn resultList
            }
        } else {
            rssDownloader = mock {
                on { execute() } doThrow RuntimeException("some error")
            }
        }
        refreshMocks()
    }

    private fun mockDatabaseLoad(success: Boolean,
                                 resultList: MutableList<PodcastDatabaseItem>) {
        val observable = when {
            success -> Observable.just(resultList)
            else -> Observable.error(Throwable("some error"))
        }

        dbProxy = mock<DbProxy> {
            on { loadAllPodcastDatabaseItemsObservable() } doReturn observable
        }

        refreshMocks()
    }

    private fun expectedItemList(amountOfItems: Int): MutableList<PodcastDatabaseItem> {
        if (amountOfItems < 1) {
            return emptyMutableList()
        }
        return (1..amountOfItems).mapTo(ArrayList<PodcastDatabaseItem>()) {
            sampleItem(it).copy(title = "$expectedTitlePrefix$it")
        }
    }

    private fun refreshMocks() {
        testApp().setSingletons(mockedDbProxy = dbProxy, rssDownloader = rssDownloader)
    }

    private fun onRecyclerViewItem(position: Int, @IdRes viewInItem: Int): ViewInteraction {
        return onRecyclerView(
                recyclerViewResId = R.id.podcast_list_fragment_recycler_view,
                itemPosition = position,
                viewInItem = viewInItem)
    }

    private fun testApp() = (activity.application as TestApp)

}
