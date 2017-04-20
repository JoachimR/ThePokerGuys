package net.thepokerguys.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.thepokerguys.R
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        setSupportActionBar(settings_toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            settings_toolbar.setNavigationOnClickListener({
                supportFinishAfterTransition()
            })
        }

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.settings_placeholder, SettingsFragment())
        transaction.commit()
    }

}