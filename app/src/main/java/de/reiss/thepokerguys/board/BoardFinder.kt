package de.reiss.thepokerguys.board

import android.content.Context
import java.util.*

object BoardFinder {

    private val amountFlopCards = 3

    private val literalPattern = "([2-9TJQKA]|10)"

    private val suitPattern = "[CcSsDdHh]"
    private val cardPattern = "$literalPattern$suitPattern"

    private val regexLiteral = Regex(literalPattern, RegexOption.IGNORE_CASE)
    private val regexSuit = Regex(suitPattern, RegexOption.IGNORE_CASE)
    private val regexOneCard = Regex(cardPattern, RegexOption.IGNORE_CASE)
    private val regexFlop = Regex("FLOP:($cardPattern){3}", RegexOption.IGNORE_CASE)
    private val regexTurn = Regex("TURN:$cardPattern", RegexOption.IGNORE_CASE)
    private val regexRiver = Regex("RIVER:$cardPattern", RegexOption.IGNORE_CASE)

    fun asTextFrom(context: Context, freeText: String): String {
        return from(freeText)?.asDisplayedText(context) ?: ""
    }

    fun from(freeText: String): Board? {
        freeText.replace(" ", "").let { text ->

            createFlopCards(text).let { flopCards ->

                if (flopCards.size == amountFlopCards) {
                    val turnCard = cardFromRegex(regexTurn, text)
                    if (turnCard != null) {
                        return Board(flopCards, turnCard, cardFromRegex(regexRiver, text))
                    } else {
                        return Board(flopCards)
                    }
                }
            }

        }
        return null
    }

    private fun createFlopCards(text: String): List<Card> {
        regexFlop.find(text)?.let { matches ->

            val result = ArrayList<Card>(amountFlopCards)
            regexOneCard.findAll(matches.value).forEach { finding ->

                val card = cardFromMatch(finding)
                if (card != null) {
                    result.add(card)
                }
            }
            return result
        }
        return emptyList()
    }

    private fun cardFromRegex(regex: Regex, text: String): Card? {
        regex.find(text)?.let { match ->

            regexOneCard.find(match.value)?.let { finding ->

                return cardFromMatch(finding)
            }
        }
        return null
    }

    private fun cardFromMatch(cardMatch: MatchResult): Card? {

        regexLiteral.find(cardMatch.value)?.let { literal ->

            if (literal.value.isEmpty().not()) {

                regexSuit.find(cardMatch.value)?.let { suit ->

                    if (suit.value.length == 1) {
                        CardLiteral.from(literal.value)?.let { cardLiteral ->

                            CardSuit.from(suit.value.first())?.let { cardSuit ->

                                return Card(cardLiteral, cardSuit)
                            }
                        }
                    }
                }
            }
        }

        return null
    }

}