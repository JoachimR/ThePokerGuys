package de.reiss.thepokerguys.play

import android.support.annotation.IdRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import de.reiss.thepokerguys.R
import de.reiss.thepokerguys.TestApp
import de.reiss.thepokerguys.board.Card
import de.reiss.thepokerguys.board.DeckOfCards
import de.reiss.thepokerguys.database.DbProxy
import de.reiss.thepokerguys.database.PodcastDatabaseItem
import de.reiss.thepokerguys.sampleItem
import de.reiss.thepokerguys.settings.AppSettings
import de.reiss.thepokerguys.settings.DisplayedCardStyle
import de.reiss.thepokerguys.testutil.EspressoTestsMatchers.withDrawable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rx.Observable


@RunWith(AndroidJUnit4::class)
open class PlayActivityTest {

    @Rule @JvmField
    val activityRule = ActivityTestRule<PlayActivity>(PlayActivity::class.java,
            true,
            false // launchActivity
    )

    private lateinit var appSettings: AppSettings
    private lateinit var dbProxy: DbProxy

    @Before
    fun setUp() {
        appSettings = mock {
            on { getSelectedCardStyleOption() } doReturn DisplayedCardStyle.Classic
        }
        dbProxy = testApp().defaultMockDbProxy()
    }

    @Test
    fun checkDisplay_classic_0() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 2c2h2s TURN:       2d        RIVER :3c blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._2c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._2h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._2s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._2d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._3c.card)
    }

    @Test
    fun checkDisplay_classic_5() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 3h3s3d TURN:       4c        RIVER :4h blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._3h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._3s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._3d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._4c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._4h.card)
    }

    @Test
    fun checkDisplay_classic_10() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 4s4d5c TURN:       5h        RIVER :5s blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._4s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._4d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._5c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._5h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._5s.card)
    }

    @Test
    fun checkDisplay_classic_15() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 5d6c6h TURN:       6s        RIVER :6d blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._5d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._6c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._6h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._6s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._6d.card)
    }

    @Test
    fun checkDisplay_classic_20() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 7c7h7s TURN:       7d        RIVER :8c blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._7c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._7h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._7s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._7d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._8c.card)
    }

    @Test
    fun checkDisplay_classic_25() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 8h8s8d TURN:       9c        RIVER :9h blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._8h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._8s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._8d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._9c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._9h.card)
    }

    @Test
    fun checkDisplay_classic_30() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 9s9d10c TURN:       10h        RIVER :10s blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._9s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._9d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Tc.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Th.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Ts.card)
    }

    @Test
    fun checkDisplay_classic_35() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """"bla
                        blablabla FLOP: 10dJcJh TURN:       Js        RIVER :Jd blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Td.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._Jc.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Jh.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Js.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Jd.card)
    }

    @Test
    fun checkDisplay_classic_40() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: QcQhQs TURN:       Qd        RIVER :Kc blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Qc.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._Qh.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Qs.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Qd.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Kc.card)
    }

    @Test
    fun checkDisplay_classic_45() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: KhKsKd TURN:       Ac        RIVER :Ah blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Kh.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._Ks.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Kd.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Ac.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Ah.card)
    }

    @Test
    fun checkDisplay_classic_46() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: AdAs2c TURN:       2h        RIVER :2d blablabla"""))
        mockSettings(DisplayedCardStyle.Classic)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Ad.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._As.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._2c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._2h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._2d.card)
    }


    @Test
    fun checkDisplay_simple_0() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 2c2h2s TURN:       2d        RIVER :3c blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._2c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._2h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._2s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._2d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._3c.card)
    }

    @Test
    fun checkDisplay_simple_5() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 3h3s3d TURN:       4c        RIVER :4h blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._3h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._3s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._3d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._4c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._4h.card)
    }

    @Test
    fun checkDisplay_simple_10() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 4s4d5c TURN:       5h        RIVER :5s blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._4s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._4d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._5c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._5h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._5s.card)
    }

    @Test
    fun checkDisplay_simple_15() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 5d6c6h TURN:       6s        RIVER :6d blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._5d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._6c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._6h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._6s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._6d.card)
    }

    @Test
    fun checkDisplay_simple_20() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 7c7h7s TURN:       7d        RIVER :8c blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._7c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._7h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._7s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._7d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._8c.card)
    }

    @Test
    fun checkDisplay_simple_25() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 8h8s8d TURN:       9c        RIVER :9h blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._8h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._8s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._8d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._9c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._9h.card)
    }

    @Test
    fun checkDisplay_simple_30() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: 9s9d10c TURN:       10h        RIVER :10s blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._9s.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._9d.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Tc.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Th.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Ts.card)
    }

    @Test
    fun checkDisplay_simple_35() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """"bla
                        blablabla FLOP: 10dJcJh TURN:       Js        RIVER :Jd blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Td.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._Jc.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Jh.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Js.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Jd.card)
    }

    @Test
    fun checkDisplay_simple_40() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: QcQhQs TURN:       Qd        RIVER :Kc blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Qc.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._Qh.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Qs.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Qd.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Kc.card)
    }

    @Test
    fun checkDisplay_simple_45() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: KhKsKd TURN:       Ac        RIVER :Ah blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Kh.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._Ks.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._Kd.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._Ac.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._Ah.card)
    }

    @Test
    fun checkDisplay_simple_46() {

        mockDatabaseLoad(success = true,
                result = sampleItem(1).copy(description = """bla
                        blablabla FLOP: AdAs2c TURN:       2h        RIVER :2d blablabla"""))
        mockSettings(DisplayedCardStyle.Simple)

        launchActivity()

        onView(withId(R.id.play_podcast_board_flop_card_1)).check(matches(isDisplayed()))

        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_1, DeckOfCards._Ad.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_2, DeckOfCards._As.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_flop_card_3, DeckOfCards._2c.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_turn_card, DeckOfCards._2h.card)
        checkPlayingCardDrawable(R.id.play_podcast_board_river_card, DeckOfCards._2d.card)
    }

    private fun checkPlayingCardDrawable(@IdRes playingCardView: Int, expectedCard: Card) {
        onView(withId(playingCardView)).check(matches(
                withDrawable(expectedCard.asDrawableRes(testApp(),
                        appSettings.getSelectedCardStyleOption()))))
    }

    private fun mockSettings(cardStyle: DisplayedCardStyle) {

        appSettings = mock {
            on { getSelectedCardStyleOption() } doReturn cardStyle
        }

        refreshMocks()
    }

    private fun mockDatabaseLoad(success: Boolean,
                                 result: PodcastDatabaseItem) {
        val observable = when {
            success -> Observable.just(result)
            else -> Observable.error(Throwable("some error"))
        }

        dbProxy = mock {
            on { loadPodcastDatabaseItemObservable(url = any<String>()) } doReturn observable
        }

        refreshMocks()
    }

    private fun refreshMocks() {
        testApp().setSingletons(appSettings = appSettings, mockedDbProxy = dbProxy)
    }

    private fun launchActivity() {
        activityRule.launchActivity(PlayActivity.createIntent(
                context = testApp(),
                url = ""))
    }

    private fun testApp(): TestApp {
        return (InstrumentationRegistry.getTargetContext().applicationContext as TestApp)
    }

}
