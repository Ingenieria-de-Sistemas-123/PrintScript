package builder

import com.printscript.parser.ParseException
import node.ASTNode
import node.DoubleExpressionNode
import node.LiteralNode
import helpers.TokenHandler
import org.printscript.token.TokenType

class ExpressionBuilder(private val h: TokenHandler) {

    fun build(): ASTNode = expr(0)

    private fun expr(minPrec: Int): ASTNode {
        var left = unary()

        while (!stopHere()) {
            val opTok = h.current()
            if (!isBinary(opTok.type)) break

            val prec = precedence(opTok.type)
            if (prec < minPrec) break

            h.advance()

            val right = expr(prec + 1)

            left = DoubleExpressionNode(left, opSymbol(opTok.type), right)
        }
        return left
    }

    private fun unary(): ASTNode =
        if (h.current().type == TokenType.MINUS) {
            h.advance()
            DoubleExpressionNode(LiteralNode("0", "number"), "-", unary())
        } else primary()

    private fun primary(): ASTNode {
        val t = h.current()
        return when (t.type) {
            TokenType.NUMBER     -> { h.advance(); LiteralNode(t.value, "number") }
            TokenType.STRING     -> { h.advance(); LiteralNode(t.value, "string") }
            TokenType.IDENTIFIER -> { h.advance(); LiteralNode(t.value, "identifier") }
            TokenType.OPEN_PAREN -> {
                h.advance()                 // consume '('
                val e = expr(0)             // parsea subexpresión
                hExpect(TokenType.CLOSE_PAREN, "Se esperaba ')'")
                e                           // NO deja el ')' pendiente
            }
            else -> throw ParseException("Expresión inválida: ${t.type}", t.line, t.column)
        }
    }


    private fun precedence(t: TokenType) = when (t) {
        TokenType.PLUS, TokenType.MINUS -> 1
        TokenType.STAR, TokenType.SLASH -> 2
        else -> -1
    }

    private fun isBinary(t: TokenType) =
        t == TokenType.PLUS || t == TokenType.MINUS || t == TokenType.STAR || t == TokenType.SLASH

    private fun stopHere(): Boolean {
        val tt = h.current().type
        return tt == TokenType.CLOSE_PAREN || tt == TokenType.SEMICOLON || tt == TokenType.EOF
    }

    private fun opSymbol(t: TokenType) = when (t) {
        TokenType.PLUS -> "+"
        TokenType.MINUS -> "-"
        TokenType.STAR -> "*"
        TokenType.SLASH -> "/"
        else -> "?"
    }

    private fun hExpect(type: TokenType, msg: String) {
        val t = h.current()
        if (t.type != type) throw ParseException("$msg. Encontré ${t.type} '${t.value}'", t.line, t.column)
        h.advance()
    }
}