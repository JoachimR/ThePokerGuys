package net.thepokerguys.board

import android.content.Context
import net.thepokerguys.R

data class Board(val flop: List<Card>,
                 val turn: Card? = null,
                 val river: Card? = null) {

    fun isValid(): Boolean {
        return flop.size == 3
    }

    fun asDisplayedText(context: Context): String {
        if (!isValid()) {
            return ""
        }
        return context.getString(R.string.board_displayed,
                flop[0], flop[1], flop[2],
                turn ?: "", river ?: "")
    }

}