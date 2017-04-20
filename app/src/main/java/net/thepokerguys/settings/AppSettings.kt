package net.thepokerguys.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import net.thepokerguys.R

open class AppSettings constructor(val context: Context) {

    private val PREF_KEY_LAST_KNOWN_LATEST_PODCAST = "PREF_KEY_LAST_KNOWN_LATEST_PODCAST"

    open fun getSettings(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    open fun getSelectedCardStyleOption(): DisplayedCardStyle {
        val selected = getSettings().getString(context.getString(R.string.pref_card_style_key),
                context.getString(R.string.pref_card_style_entry_default_value))
        return DisplayedCardStyle.fromPreferenceEntry(context, selected)
    }

    open fun isNoisyAware(): Boolean {
        return getSettings().getBoolean(
                context.getString(R.string.pref_noisy_aware_key), true)
    }

    open fun shouldWarnWhenNoWifi(): Boolean {
        return getSettings().getBoolean(
                context.getString(R.string.pref_warn_no_wifi_key), true)
    }

    open fun shouldNotifyNewPodcast(): Boolean {
        return getSettings().getBoolean(
                context.getString(R.string.pref_notify_new_podcast_key), true)
    }

    open fun getLastKnownLatestPodcastURL(): String {
        return getSettings().getString(PREF_KEY_LAST_KNOWN_LATEST_PODCAST, "")
    }

    open fun setLastKnownLatestPodcastURL(url: String) {
        getSettings()
                .edit()
                .putString(PREF_KEY_LAST_KNOWN_LATEST_PODCAST, url)
                .apply()
    }

}