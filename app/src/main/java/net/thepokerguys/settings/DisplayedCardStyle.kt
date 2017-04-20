package net.thepokerguys.settings

import android.content.Context
import android.support.annotation.StringRes
import net.thepokerguys.R

enum class DisplayedCardStyle(@StringRes val preferenceEntry: Int, val drawablePrefix: String) {

    Classic(R.string.pref_card_style_entry_classic, "card_classic_"),
    Simple(R.string.pref_card_style_entry_simple, "card_simple_");

    fun preferenceEntry(context: Context): String = context.getString(preferenceEntry)

    companion object {

        val values = values()

        fun fromPreferenceEntry(context: Context, preferenceEntry: String): DisplayedCardStyle {
            return values.firstOrNull { it.preferenceEntry(context) == preferenceEntry } ?: Classic
        }

    }

}