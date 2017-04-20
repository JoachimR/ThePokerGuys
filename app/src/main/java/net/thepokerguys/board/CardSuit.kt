package net.thepokerguys.board

enum class CardSuit constructor(val emoji: Int = 0x2663) {

    Clubs(0x2663), Hearts(0x2665), Spades(0x2660), Diamonds(0x2666);

    companion object {

        fun from(suit: Char): CardSuit? {
            if (suit == 'c' || suit == 'C') {
                return CardSuit.Clubs
            }
            if (suit == 'h' || suit == 'H') {
                return CardSuit.Hearts
            }
            if (suit == 's' || suit == 'S') {
                return CardSuit.Spades
            }
            if (suit == 'd' || suit == 'D') {
                return CardSuit.Diamonds
            }
            return null
        }

    }

    override fun toString(): String {
        return this.name.substring(0, 1).toLowerCase()
    }

    fun asEmoji(): String {
        return String(Character.toChars(emoji))
    }

}
