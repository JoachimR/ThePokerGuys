package net.thepokerguys

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.main_activity.*
import net.thepokerguys.audio.AudioPlayerService
import net.thepokerguys.eventbus.AppEventBus
import net.thepokerguys.eventbus.DownloadConfirmed
import net.thepokerguys.eventbus.PlayButtonStateChanged
import net.thepokerguys.list.PodcastListFragment
import net.thepokerguys.settings.SettingsActivity
import net.thepokerguys.util.showDialogFragment
import net.thepokerguys.util.trueAsVisible
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

    }

    private lateinit var playOrPause: ImageView

    private lateinit var podcastListFragment: PodcastListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        playOrPause = findViewById<ImageView>(R.id.main_play_or_pause)
        playOrPause.setOnClickListener {
            AudioPlayerService.instance?.playOrPausePodcast()
        }

        setupNavDrawer(toolbar)

        podcastListFragment = PodcastListFragment()
        replaceFragment(podcastListFragment)

        window.setBackgroundDrawable(ColorDrawable(Color.WHITE)) // remove splash icon
    }

    private fun setupNavDrawer(toolbar: Toolbar) {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        @Suppress("DEPRECATION")
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        (main_nav_view.getHeaderView(0)
                .findViewById<TextView>(R.id.main_nav_view_header_version))
                .text = app().formattedAppVersion

        main_nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
        AppEventBus.register(this)
    }

    override fun onStop() {
        AppEventBus.unregister(this)
        super.onStop()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            podcastListFragment.onTouchActionDownEvent(ev.x, ev.y)
        }
        return super.dispatchTouchEvent(ev)
    }

    @Subscribe
    fun onEvent(event: DownloadConfirmed) {
        downloadManagerHelper().startNewDownloadRequest(event.url)
    }

    @Subscribe
    fun onEvent(event: PlayButtonStateChanged) {
        updatePlayButtonState(event.isSetToSomeFile, event.isCurrentlyPlaying)
    }

    private fun updatePlayButtonState(show: Boolean, isPlaying: Boolean) {
        playOrPause.visibility = show.trueAsVisible()
        playOrPause.setImageResource(
                when {
                    isPlaying -> R.drawable.ic_pause_circle_outline_black_24dp
                    else -> R.drawable.ic_play_circle_outline_black_24dp
                })
    }

    private fun replaceFragment(newFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.replace(R.id.main_fragment_container, newFragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_website -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(HOMEPAGE_URL)))
            }
            R.id.nav_youtube -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL)))
            }
            R.id.nav_twitter -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_URL)))
            }
            R.id.nav_settings -> {
                startActivity(SettingsActivity.createIntent(this))
            }
            R.id.nav_info -> {
                showDialogFragment(AppInfoDialog())
            }
            else -> {
                throw RuntimeException("unknown navigation item ${item.itemId}")
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}
