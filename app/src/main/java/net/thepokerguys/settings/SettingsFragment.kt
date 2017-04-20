package net.thepokerguys.settings

import android.os.Bundle
import android.preference.PreferenceFragment
import net.thepokerguys.R
import net.thepokerguys.util.checkIfGooglePlayServicesInstalled


class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onStart() {
        super.onStart()
        disableOrEnablePlayServiceSettings()
    }

    private fun disableOrEnablePlayServiceSettings() {
        val enable = activity.checkIfGooglePlayServicesInstalled()
        enableOrDisablePlayServicePreferences(enable)
    }

    private fun enableOrDisablePlayServicePreferences(enable: Boolean) {
        findPreference(getString(R.string.pref_notify_new_podcast_key))?.let {
            it.isEnabled = enable
        }

        if (enable.not()) {
            val editor = AppSettings(activity).getSettings().edit()
            editor.putBoolean(getString(R.string.pref_notify_new_podcast_key), false)
            editor.apply()
        }
    }

}