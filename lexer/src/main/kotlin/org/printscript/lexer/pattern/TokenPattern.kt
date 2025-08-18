package org.printscript.lexer.pattern
import org.printscript.token.TokenType


internal object TokenPattern {
     val tokenSpecs: List<Pair<TokenType, Regex>> = listOf(
        TokenType.LET to Regex("""\blet\b"""),
        TokenType.PRINTLN to Regex("""\bprintln\b"""),
        TokenType.NUMBER_TYPE to Regex("""\bnumber\b"""),
        TokenType.STRING_TYPE to Regex("""\bstring\b"""),

        // literales
        TokenType.STRING to Regex(""""(?:\\.|[^"\\\n])*""""),
        TokenType.STRING to Regex("""'(?:\\.|[^'\\\n])*'"""),
        TokenType.NUMBER to Regex("""\d+(?:\.\d+)?"""),

        // operadores/símbolos
        TokenType.EQUAL to Regex("""="""),
        TokenType.PLUS to Regex("""\+"""),
        TokenType.MINUS to Regex("""-"""),
        TokenType.STAR to Regex("""\*"""),
        TokenType.SLASH to Regex("""/"""),
        TokenType.SEMICOLON to Regex(""";"""),
        TokenType.COLON to Regex(""":"""),
        TokenType.OPEN_PAREN to Regex("""\("""),
        TokenType.CLOSE_PAREN to Regex("""\)"""),

        // identificadores (al final)
        TokenType.IDENTIFIER to Regex("""[A-Za-z_][A-Za-z0-9_]*""")
    )
}

/*Por qué object?*/
/*Porque no necesito instanciarlo, es un objeto que contiene una lista de pares (TokenType, Regex).
 * No tiene estado, no tiene métodos, solo es una lista de pares.
 * Es un singleton, no necesito crear instancias de él.
 * Es un objeto que contiene la configuración del lexer,
 * no es un objeto que representa un token en sí mismo.
 * Esto hace que Lexer no tenga que crear objetos extra.*/