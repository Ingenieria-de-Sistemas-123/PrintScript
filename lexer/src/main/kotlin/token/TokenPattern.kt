package org.printscript.token

class TokenPattern {
     val tokenSpecs: List<Pair<TokenType, Regex>> = listOf(
        TokenType.LET to Regex("""\blet\b"""),
        TokenType.PRINTLN to Regex("""\bprintln\b"""),
        TokenType.NUMBER_TYPE to Regex("""\bnumber\b"""),
        TokenType.STRING_TYPE to Regex("""\bstring\b"""),

        // literales
        TokenType.STRING to Regex(""""(?:\\.|[^"\\])*""""),
        TokenType.STRING to Regex("""'(?:\\.|[^'\\])*'"""),
        TokenType.NUMBER to Regex("""\d+(?:\.\d+)?"""),

        // operadores/símbolos
        TokenType.EQUAL to Regex("""="""),
        TokenType.PLUS to Regex("""\+"""),
        TokenType.MINUS to Regex("""-"""),
        TokenType.STAR to Regex("""\*"""),
        TokenType.SLASH to Regex("""/"""),
        TokenType.SEMICOLON to Regex(""";"""),
        TokenType.SEPARATOR to Regex(""":"""),
        TokenType.OPEN_PAREN to Regex("""\("""),
        TokenType.CLOSE_PAREN to Regex("""\)"""),

        // identificadores (al final)
        TokenType.IDENTIFIER to Regex("""[A-Za-z_][A-Za-z0-9_]*""")
    )
}