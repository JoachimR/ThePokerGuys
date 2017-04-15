package de.reiss.thepokerguys.list

import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import de.reiss.thepokerguys.TestApp
import de.reiss.thepokerguys.database.DbProxy
import de.reiss.thepokerguys.database.PodcastDatabaseItem
import de.reiss.thepokerguys.sampleList
import de.reiss.thepokerguys.testutil.FragmentTest
import de.reiss.thepokerguys.util.emptyMutableList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rx.Observable

@RunWith(AndroidJUnit4::class)
class PodcastListPresenterTest : FragmentTest<PodcastListPresenter>() {

    val presenterUnderTest = PresenterUnderTest()

    override fun onCreateFragment(): PodcastListPresenter {
        return presenterUnderTest
    }

    @Before
    fun setUp() {
        mockLoadFromDatabase(success = true, resultList = emptyMutableList())
        launchFragment()
    }

    private fun testApp() = (activity.application as TestApp)

    @Test
    fun downloadFail() {
        mockLoadFromDatabase(success = false, resultList = emptyMutableList())

        presenterUnderTest.downloadRssList()

        assertTrue(presenterUnderTest.mvpModel.listItems.isEmpty())
    }

    @Test
    fun downloadSuccessEmpty() {
        mockLoadFromDatabase(success = true, resultList = emptyMutableList())

        presenterUnderTest.downloadRssList()

        assertTrue(presenterUnderTest.mvpModel.listItems.isEmpty())
    }

    @Test
    fun downloadSuccessOneItem() {
        mockLoadFromDatabase(success = true, resultList = sampleList(1))

        presenterUnderTest.downloadRssList()

        assertEquals(1, presenterUnderTest.mvpModel.listItems.size)
    }

    @Test
    fun downloadSuccessTwoItems() {
        mockLoadFromDatabase(success = true, resultList = sampleList(2))

        presenterUnderTest.downloadRssList()

        assertEquals(2, presenterUnderTest.mvpModel.listItems.size)
    }

    @Test
    fun downloadSuccess999Items() {
        mockLoadFromDatabase(success = true, resultList = sampleList(99))

        presenterUnderTest.downloadRssList()

        assertEquals(99, presenterUnderTest.mvpModel.listItems.size)
    }

    private fun mockLoadFromDatabase(success: Boolean,
                                     resultList: MutableList<PodcastDatabaseItem>) {

        val observable = when {
            success -> Observable.just(resultList)
            else -> Observable.error(Throwable("some error"))
        }

        testApp().setSingletons(
                mockedDbProxy = mock<DbProxy> {
                    on { loadAllPodcastDatabaseItemsObservable() } doReturn observable
                }
        )
    }

    class PresenterUnderTest : PodcastListPresenter() {

        internal var view: PodcastList.View = mock()

        override val mvpView: PodcastList.View?
            get() {
                return view
            }

    }

}
