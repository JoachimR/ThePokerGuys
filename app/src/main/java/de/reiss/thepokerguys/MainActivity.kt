package de.reiss.thepokerguys

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
import de.reiss.thepokerguys.audio.AudioPlayerService
import de.reiss.thepokerguys.eventbus.AppEventBus
import de.reiss.thepokerguys.eventbus.DownloadConfirmed
import de.reiss.thepokerguys.eventbus.PlayButtonStateChanged
import de.reiss.thepokerguys.list.PodcastListFragment
import de.reiss.thepokerguys.settings.SettingsActivity
import de.reiss.thepokerguys.util.showDialogFragment
import de.reiss.thepokerguys.util.trueAsVisible
import kotlinx.android.synthetic.main.main_activity.*
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var playOrPause: ImageView

    private lateinit var podcastListFragment: PodcastListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar = findViewById(R.id.main_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        playOrPause = findViewById(R.id.main_play_or_pause) as ImageView
        playOrPause.setOnClickListener {
            AudioPlayerService.instance?.playOrPausePodcast()
        }

        setupNavDrawer(toolbar)

        podcastListFragment = PodcastListFragment()
        replaceFragment(podcastListFragment)

        window.setBackgroundDrawable(ColorDrawable(Color.WHITE)) // remove splash icon
    }

    private fun setupNavDrawer(toolbar: Toolbar) {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        @Suppress("DEPRECATION")
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        (main_nav_view.getHeaderView(0)
                .findViewById(R.id.main_nav_view_header_version) as TextView)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println("AAAAAAAA" + intent?.dataString) // TODO
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
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

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}
