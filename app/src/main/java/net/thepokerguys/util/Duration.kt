package net.thepokerguys.util

object Duration {

    val MILLISECOND: Long = 1L
    val SECOND = MILLISECOND * 1000
    val MINUTE = SECOND * 60
    val HOUR = MINUTE * 60
    val DAY = HOUR * 24
    val WEEK = DAY * 7

    fun milliseconds(milliseconds: Long): Long {
        return milliseconds
    }

    fun seconds(seconds: Long): Long {
        return seconds * SECOND
    }

    fun minutes(minutes: Long): Long {
        return minutes * MINUTE
    }

    fun hours(hours: Long): Long {
        return hours * HOUR
    }

    fun days(days: Long): Long {
        return days * DAY
    }

    fun weeks(weeks: Long): Long {
        return weeks * WEEK
    }

}
