package org.printscript.lexer.pattern
import org.printscript.token.TokenType


data object DefaultTokenProvider : TokenProvider {

   private val keywords = TokenProvider.builder(
      mapOf(
         """\blet\b""" to TokenType.LET,
         """\bprintln\b""" to TokenType.PRINTLN,
         """\bnumber\b""" to TokenType.NUMBER_TYPE,
         """\bstring\b""" to TokenType.STRING_TYPE,
         ))

   private val literals = TokenProvider.builder(
      mapOf(
         """"(?:\\.|[^"\\\n])*"""" to TokenType.STRING,
         """'(?:\\.|[^'\\\n])*'""" to TokenType.STRING,
         """\d+(?:\.\d+)?""" to TokenType.NUMBER))

   private val symbols = TokenProvider.builder(
      mapOf(
         """=""" to TokenType.EQUAL,
         """\+""" to TokenType.PLUS,
         """-""" to TokenType.MINUS,
         """\*""" to TokenType.STAR,
         """/""" to TokenType.SLASH,
         """;""" to TokenType.SEMICOLON,
         """:""" to TokenType.COLON,
         """\(""" to TokenType.OPEN_PAREN,
         """\)""" to TokenType.CLOSE_PAREN,))

   private val identifiers = TokenProvider.builder(
      mapOf("""[A-Za-z_][A-Za-z0-9_]*""" to TokenType.IDENTIFIER))


   private val provider = keywords + literals + symbols + identifiers

   override fun matchAt(input: CharSequence, pos: Int) = provider.matchAt(input, pos)

   override fun plus(other: TokenProvider): TokenProvider = (provider + other)
}
/*Por qué object?*/
/*Porque no necesito instanciarlo, es un objeto que contiene una lista de pares (TokenType, Regex).
 * No tiene estado, no tiene métodos, solo es una lista de pares.
 * Es un singleton, no necesito crear instancias de él.
 * Es un objeto que contiene la configuración del lexer,
 * no es un objeto que representa un token en sí mismo.
 * Esto hace que Lexer no tenga que crear objetos extra.*/