package net.thepokerguys.util

import android.content.Context
import android.database.Cursor
import android.text.format.DateUtils
import android.view.View
import java.util.*
import java.util.concurrent.TimeUnit

fun <T> emptyMutableList(): MutableList<T> = ArrayList<T>()

fun Boolean.trueAsVisible(): Int = if (this) View.VISIBLE else View.GONE
fun Boolean.trueAsGone(): Int = if (this) View.GONE else View.VISIBLE

fun Int.asDisplayedDuration(locale: Locale = Locale.getDefault()): String {
    return String.format(locale, "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(this.toLong()),
            TimeUnit.MILLISECONDS.toMinutes(this.toLong()) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(this.toLong()) % TimeUnit.MINUTES.toSeconds(1))
}

fun View.fadeInOrFadeOut(fadeIn: Boolean) {
    this.animate().alpha(if (fadeIn) 1.0f else 0.0f)
}

fun Date.asFormattedDate(context: Context): String = DateUtils.formatDateTime(context, this.time,
        (DateUtils.FORMAT_ABBREV_MONTH
                or DateUtils.FORMAT_SHOW_DATE
                or DateUtils.FORMAT_SHOW_YEAR))

fun Cursor.getLong(column: String): Long {
    return this.getLong(this.getColumnIndex(column))
}

fun Cursor.getInt(column: String): Int {
    return this.getInt(this.getColumnIndex(column))
}

fun Cursor.getString(column: String): String {
    return this.getString(this.getColumnIndex(column))
}