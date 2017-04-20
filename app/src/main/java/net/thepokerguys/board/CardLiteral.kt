package net.thepokerguys.board

enum class CardLiteral(val textualRepresentation: String) {

    _2("2"), _3("3"), _4("4"), _5("5"),
    _6("6"), _7("7"), _8("8"), _9("9"),
    _T("T"), _J("J"), _K("K"), _Q("Q"),
    _A("A");

    companion object {

        fun from(string: String): CardLiteral? {
            if (string == "10") {
                return _T
            }
            values().forEach {
                if (it.textualRepresentation == string) {
                    return it
                }
            }
            return null
        }

    }

    override fun toString(): String {
        return textualRepresentation
    }

}