package net.thepokerguys.board

import android.content.Context
import android.support.annotation.DrawableRes
import net.thepokerguys.settings.DisplayedCardStyle

data class Card(val literal: CardLiteral,
                val suit: CardSuit) {

    @DrawableRes
    fun asDrawableRes(context: Context, cardStyle: DisplayedCardStyle): Int {
        val text = "${cardStyle.drawablePrefix}$literal$suit".toLowerCase()
        return context.resources.getIdentifier(text, "drawable", context.packageName)
    }

    override fun toString(): String {
        return String.format("%s%s", literal, suit.asEmoji())
    }

}