package de.reiss.thepokerguys

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

object LocalStatePreferences {

    private val KEY_RECENT_SEARCH_HISTORY = "KEY_RECENT_SEARCH_HISTORY"

    fun register(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener)
    }

    fun get(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun isSortDescending(context: Context): Boolean {
        return get(context).getBoolean("SortDescending", true)
    }

    fun putAndApplySortDescending(context: Context, descending: Boolean) {
        val editor = get(context).edit()
        editor.putBoolean("SortDescending", descending)
        editor.apply()
    }

    fun getLastModifiedRssTime(context: Context): Long {
        return get(context).getLong("LastModifiedRssTime", -1L)
    }

    fun getRecentSearchHistorySet(context: Context): Set<String> {
        return get(context).getStringSet(KEY_RECENT_SEARCH_HISTORY, Collections.emptySet())
    }

    fun addToAndApplyRecentSearchHistorySet(context: Context, searchTerm: String) {
        val currentHistory = getRecentSearchHistorySet(context).toMutableList()

        currentHistory.add(searchTerm)
        if (currentHistory.size > 10) {
            currentHistory.removeAt(0)
        }

        val editor = get(context).edit()
        editor.putStringSet(KEY_RECENT_SEARCH_HISTORY, currentHistory.toSet())
        editor.apply()
    }

}