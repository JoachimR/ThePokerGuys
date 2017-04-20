package net.thepokerguys.board

import org.junit.Assert.*
import org.junit.Test

class BoardFinderTest {

    @Test
    fun when_no_description_then_no_board_parsed() {
        val podcastDescription = ""

        val board = BoardFinder.from(podcastDescription)

        assertNull(board)
    }

    @Test
    fun when_wrong_description_then_no_board_parsed() {

        BoardFinder.from(
                """blablabla

                FLOP: KdTc RIVER :5h

                For access to exclusive Poker Guys freerolls blablabla
        """).let(::assertNull)
    }

    @Test
    fun when_valid_description_then_correct_board_parsed() {

        boardFromText(
                """blablabla

                FLOP: KdTc2s      TURN:       10h          RIVER :5h

                For access to exclusive Poker Guys freerolls blablabla
        """).let { board ->

            board.flop.let { flop ->
                assertCardIsCorrect(flop[0], CardLiteral._K, CardSuit.Diamonds)
                assertCardIsCorrect(flop[1], CardLiteral._T, CardSuit.Clubs)
                assertCardIsCorrect(flop[2], CardLiteral._2, CardSuit.Spades)
            }
            assertCardIsCorrect(board.turn!!, CardLiteral._T, CardSuit.Hearts)
            assertCardIsCorrect(board.river!!, CardLiteral._5, CardSuit.Hearts)
        }
    }

    @Test
    fun when_valid_description_with_only_flop_then_correct_board_parsed() {

        boardFromText(
                """blablabla

                FLOP: KdTc2s

                For access to exclusive Poker Guys freerolls blablabla
        """).let { board ->

            board.flop.let { flop ->
                assertCardIsCorrect(flop[0], CardLiteral._K, CardSuit.Diamonds)
                assertCardIsCorrect(flop[1], CardLiteral._T, CardSuit.Clubs)
                assertCardIsCorrect(flop[2], CardLiteral._2, CardSuit.Spades)
            }
        }
    }

    @Test
    fun when_valid_description_with_only_flop_and_turn_then_correct_board_parsed() {

        boardFromText(
                """blablabla

                FLOP: KdTc2s      TURN:       10h

                For access to exclusive Poker Guys freerolls blablabla
        """).let { board ->

            board.flop.let { flop ->
                assertCardIsCorrect(flop[0], CardLiteral._K, CardSuit.Diamonds)
                assertCardIsCorrect(flop[1], CardLiteral._T, CardSuit.Clubs)
                assertCardIsCorrect(flop[2], CardLiteral._2, CardSuit.Spades)
            }

            assertCardIsCorrect(board.turn!!, CardLiteral._T, CardSuit.Hearts)
        }
    }

    @Test
    fun when_valid_description_but_invalid_sequence_of_cards_then_still_board_parsed() {

        boardFromText(
                """blablabla

                    FLOP: AdAcAs TURN: 10h RIVER :Ad

                    For access to exclusive Poker Guys freerolls blablabla
            """).let { board ->

            board.flop.let { flop ->
                assertCardIsCorrect(flop[0], CardLiteral._A, CardSuit.Diamonds)
                assertCardIsCorrect(flop[1], CardLiteral._A, CardSuit.Clubs)
                assertCardIsCorrect(flop[2], CardLiteral._A, CardSuit.Spades)
            }
            assertCardIsCorrect(board.turn!!, CardLiteral._T, CardSuit.Hearts)
            assertCardIsCorrect(board.river!!, CardLiteral._A, CardSuit.Diamonds)
        }
    }

    private fun boardFromText(freeText: String): Board {
        val board = BoardFinder.from(freeText.trimIndent())
        assertNotNull(board)
        return board!!
    }

    private fun assertCardIsCorrect(actual: Card,
                                    expectedLiteral: CardLiteral,
                                    expectedSuit: CardSuit) {
        assertEquals(expectedLiteral, actual.literal)
        assertEquals(expectedSuit, actual.suit)
        assertEquals(DeckOfCards.findCardInDeck(expectedLiteral, expectedSuit)!!, actual)
    }

}