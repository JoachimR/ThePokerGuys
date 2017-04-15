package de.reiss.thepokerguys.board

enum class DeckOfCards(val card: Card) {

    _2c(Card(CardLiteral._2, CardSuit.Clubs)),
    _2h(Card(CardLiteral._2, CardSuit.Hearts)),
    _2s(Card(CardLiteral._2, CardSuit.Spades)),
    _2d(Card(CardLiteral._2, CardSuit.Diamonds)),
    _3c(Card(CardLiteral._3, CardSuit.Clubs)),
    _3h(Card(CardLiteral._3, CardSuit.Hearts)),
    _3s(Card(CardLiteral._3, CardSuit.Spades)),
    _3d(Card(CardLiteral._3, CardSuit.Diamonds)),
    _4c(Card(CardLiteral._4, CardSuit.Clubs)),
    _4h(Card(CardLiteral._4, CardSuit.Hearts)),
    _4s(Card(CardLiteral._4, CardSuit.Spades)),
    _4d(Card(CardLiteral._4, CardSuit.Diamonds)),
    _5c(Card(CardLiteral._5, CardSuit.Clubs)),
    _5h(Card(CardLiteral._5, CardSuit.Hearts)),
    _5s(Card(CardLiteral._5, CardSuit.Spades)),
    _5d(Card(CardLiteral._5, CardSuit.Diamonds)),
    _6c(Card(CardLiteral._6, CardSuit.Clubs)),
    _6h(Card(CardLiteral._6, CardSuit.Hearts)),
    _6s(Card(CardLiteral._6, CardSuit.Spades)),
    _6d(Card(CardLiteral._6, CardSuit.Diamonds)),
    _7c(Card(CardLiteral._7, CardSuit.Clubs)),
    _7h(Card(CardLiteral._7, CardSuit.Hearts)),
    _7s(Card(CardLiteral._7, CardSuit.Spades)),
    _7d(Card(CardLiteral._7, CardSuit.Diamonds)),
    _8c(Card(CardLiteral._8, CardSuit.Clubs)),
    _8h(Card(CardLiteral._8, CardSuit.Hearts)),
    _8s(Card(CardLiteral._8, CardSuit.Spades)),
    _8d(Card(CardLiteral._8, CardSuit.Diamonds)),
    _9c(Card(CardLiteral._9, CardSuit.Clubs)),
    _9h(Card(CardLiteral._9, CardSuit.Hearts)),
    _9s(Card(CardLiteral._9, CardSuit.Spades)),
    _9d(Card(CardLiteral._9, CardSuit.Diamonds)),
    _Tc(Card(CardLiteral._T, CardSuit.Clubs)),
    _Th(Card(CardLiteral._T, CardSuit.Hearts)),
    _Ts(Card(CardLiteral._T, CardSuit.Spades)),
    _Td(Card(CardLiteral._T, CardSuit.Diamonds)),
    _Jc(Card(CardLiteral._J, CardSuit.Clubs)),
    _Jh(Card(CardLiteral._J, CardSuit.Hearts)),
    _Js(Card(CardLiteral._J, CardSuit.Spades)),
    _Jd(Card(CardLiteral._J, CardSuit.Diamonds)),
    _Qc(Card(CardLiteral._Q, CardSuit.Clubs)),
    _Qh(Card(CardLiteral._Q, CardSuit.Hearts)),
    _Qs(Card(CardLiteral._Q, CardSuit.Spades)),
    _Qd(Card(CardLiteral._Q, CardSuit.Diamonds)),
    _Kc(Card(CardLiteral._K, CardSuit.Clubs)),
    _Kh(Card(CardLiteral._K, CardSuit.Hearts)),
    _Ks(Card(CardLiteral._K, CardSuit.Spades)),
    _Kd(Card(CardLiteral._K, CardSuit.Diamonds)),
    _Ac(Card(CardLiteral._A, CardSuit.Clubs)),
    _Ah(Card(CardLiteral._A, CardSuit.Hearts)),
    _As(Card(CardLiteral._A, CardSuit.Spades)),
    _Ad(Card(CardLiteral._A, CardSuit.Diamonds));

    companion object {

        fun findCardInDeck(literal: CardLiteral, suit: CardSuit): Card? {
            values().forEach {
                if (literal == it.card.literal) {
                    if (suit == it.card.suit) {
                        return it.card
                    }
                }
            }
            return null
        }

    }

}
