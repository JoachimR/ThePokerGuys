package net.thepokerguys.play

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import net.thepokerguys.AppActivity
import net.thepokerguys.R
import net.thepokerguys.board.Board
import net.thepokerguys.board.BoardFinder
import net.thepokerguys.board.Card
import net.thepokerguys.database.PodcastDatabaseItem
import net.thepokerguys.settings.DisplayedCardStyle
import net.thepokerguys.util.*
import kotlinx.android.synthetic.main.board.*
import kotlinx.android.synthetic.main.board_cards.*
import kotlinx.android.synthetic.main.play_activity.*
import kotlinx.android.synthetic.main.play_podcast_loaded.*

class PlayActivity : AppActivity(), Play.View {

    companion object {

        val KEY_PODCAST_URL = "KEY_PODCAST_URL"

        fun createIntent(context: Context, url: String): Intent {
            return Intent(context, PlayActivity::class.java)
                    .putExtra(KEY_PODCAST_URL, url)
        }

    }

    private lateinit var presenter: Play.Presenter

    private lateinit var cardStyle: DisplayedCardStyle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_activity)

        presenter = initPresenter(supportFragmentManager)

        val toolbar = findViewById(R.id.play_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        cardStyle = appSettings().getSelectedCardStyleOption()

        initLayout()
    }

    private fun initPresenter(fragmentManager: FragmentManager): Play.Presenter {
        var presenter = fragmentManager.findFragmentByTag("PlayPresenter")
        if (presenter == null) {
            try {
                presenter = PlayPresenter.createInstance(
                        intent.getStringExtra(PlayActivity.KEY_PODCAST_URL))

            } catch (e: InstantiationException) {
                throw RuntimeException("Could not instantiate presenter fragment", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Could not instantiate presenter fragment", e)
            }

            @Suppress
            fragmentManager.beginTransaction().add(presenter, "PlayPresenter").commitNow()
        }

        return presenter as PlayPresenter
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initLayout()
    }

    private fun initLayout() {
        play_seek_bar.setPadding(0, 0, 0, 0)
        play_seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangedByUserListener() {

            override fun changedByUserTo(progress: Int) {
                presenter.changeProgress(progress)
            }

        })

        play_minus_10_sec.setOnClickListener({
            presenter.goBack10Seconds()
        })

        play_plus_10_sec.setOnClickListener({
            presenter.goForward10Seconds()
        })

        play_big_fab.setOnClickListener({
            presenter.mvpModel.let { model ->

                when {
                    model.isPodcastFullyDownloaded -> presenter.playOrPause()
                    else ->
                        when {
                            model.isPodcastCurrentlyBeingDownloaded -> presenter.cancelDownload()
                            else -> presenter.startDownload()
                        }
                }
            }
        })

        play_controls_file_start_download.setOnClickListener {
            presenter.startDownload()
        }
        play_controls_file_cancel_download.setOnClickListener {
            presenter.cancelDownload()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            play_speed_buttons_root.visibility = VISIBLE
            play_speed_buttons_root.setOnCheckedChangeListener { group, checkedId ->
                presenter.setSpeed(
                        when (checkedId) {
                            R.id.play_speed_1_25 -> 1.25f
                            R.id.play_speed_1_5 -> 1.5f
                            else -> 1f
                        })
            }
        } else {
            play_speed_buttons_root.visibility = GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.play, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            supportFinishAfterTransition()
            return true
        } else if (item.itemId == R.id.menu_action_share) {
            presenter.showShareDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setMVPModel(model: Play.Model) {
        runOnUiThread {
            setTopView(model)
            setBottomView(model)
        }
    }

    private fun setTopView(model: Play.Model) {
        model.podcast.let { podcast ->

            if (podcast == null) {
                play_podcast_root_loading.visibility = VISIBLE

                play_podcast_root_loaded.visibility = GONE
                play_seek_bar_root.visibility = GONE
            } else {
                play_podcast_root_loading.visibility = model.isLoading.trueAsVisible()
                play_podcast_root_loaded.visibility = model.isLoading.trueAsGone()
                play_seek_bar_root.visibility = model.isLoading.trueAsGone()

                setBoardLayout(model)

                play_podcast_title.text = podcast.title
                play_podcast_publish_date.text = podcast.publishedDate.asFormattedDate(this)
                play_podcast_decription.text = podcast.description
            }
        }
    }

    private fun setBoardLayout(model: Play.Model) {
        model.podcast?.let { podcast ->

            BoardFinder.from(podcast.description).let { board ->

                if (board == null) {
                    hideBoardLayout()
                } else {
                    displayBoardLayout(board)
                }
            }
        }
    }

    private fun hideBoardLayout() {
        play_appbar.setExpanded(false)
        play_podcast_root_loaded.isNestedScrollingEnabled = false

        play_podcast_board_card_root.visibility = GONE
    }

    private fun displayBoardLayout(board: Board) {
        play_appbar.setExpanded(true)
        play_podcast_board_card_root.visibility = VISIBLE

        play_podcast_board_flop_card_1.setImageResource(drawableRes(board.flop[0]))
        play_podcast_board_flop_card_2.setImageResource(drawableRes(board.flop[1]))
        play_podcast_board_flop_card_3.setImageResource(drawableRes(board.flop[2]))

        if (board.turn == null) {
            play_podcast_board_turn_card.visibility = INVISIBLE
        } else {
            play_podcast_board_turn_card.visibility = VISIBLE
            play_podcast_board_turn_card.setImageResource(drawableRes(board.turn))
        }

        if (board.river == null) {
            play_podcast_board_river_card.visibility = INVISIBLE
        } else {
            play_podcast_board_river_card.visibility = VISIBLE
            play_podcast_board_river_card.setImageResource(drawableRes(board.river))
        }
    }

    private fun setBottomView(model: Play.Model) {
        model.podcast.let { podcast ->

            if (podcast == null) {
                play_controls_root.visibility = GONE
            } else {
                play_controls_root.visibility = VISIBLE

                setPlayControls(model, podcast)
            }
        }
    }

    private fun setPlayControls(model: Play.Model, podcast: PodcastDatabaseItem) {
        if (model.isPodcastFullyDownloaded) {
            play_controls_file_fully_downloaded_root.visibility = VISIBLE
            play_seek_bar.visibility = VISIBLE
            play_controls_file_not_fully_downloaded_root.visibility = GONE

            play_big_fab.setImageResource(
                    when {
                        model.isPodcastPlaying ->
                            R.drawable.ic_pause_black_24dp
                        else ->
                            R.drawable.ic_play_arrow_black_24dp
                    })

            val isMarshMallow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            play_speed_buttons_root.visibility = isMarshMallow.trueAsVisible()

            setAudioProgress(podcast.progress, podcast.duration)
            selectSpeedButton(podcast.speed)

        } else {
            play_controls_file_fully_downloaded_root.visibility = GONE
            play_seek_bar.visibility = GONE
            play_controls_file_not_fully_downloaded_root.visibility = VISIBLE

            play_controls_file_start_download.visibility =
                    model.isPodcastCurrentlyBeingDownloaded.trueAsGone()
            play_controls_file_cancel_download.visibility =
                    model.isPodcastCurrentlyBeingDownloaded.trueAsVisible()
        }
    }

    override fun setAudioProgress(progressInMs: Int, maxInMs: Int) {
        val progress = if (maxInMs <= 0) 0 else progressInMs

        play_time_current.text = progress.asDisplayedDuration()
        play_time_max.text = maxInMs.asDisplayedDuration()
        play_seek_bar.post {
            play_seek_bar.max = maxInMs
            play_seek_bar.progress = progress
        }
    }

    override fun selectSpeedButton(speed: Float) {
        when (speed) {
            1.25f -> play_speed_buttons_root.check(R.id.play_speed_1_25)
            1.5f -> play_speed_buttons_root.check(R.id.play_speed_1_5)
            else -> play_speed_buttons_root.check(R.id.play_speed_1)
        }
    }

    @DrawableRes
    fun drawableRes(card: Card): Int {
        return card.asDrawableRes(this, cardStyle)
    }

}
