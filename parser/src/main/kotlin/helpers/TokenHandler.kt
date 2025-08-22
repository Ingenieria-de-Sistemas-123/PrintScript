package helpers

import com.printscript.parser.ParseException
import org.printscript.token.Token
import org.printscript.token.TokenType

class TokenHandler(private val tokens: List<Token>) {
    var pos: Int = 0
        private set

    fun current(): Token = tokens[pos]
    fun atEnd(): Boolean = current().type == TokenType.EOF
    fun advance(): Token = current().also { if (!atEnd()) pos++ }

    fun match(type: TokenType): Boolean {
        if (current().type == type) { advance(); return true }
        return false
    }

    fun expect(type: TokenType, msg: String): Token {
        val t = current()
        if (t.type == type) return advance()
        throw ParseException("$msg. Encontré ${t.type} '${t.value}'", t.line, t.column)
    }
}