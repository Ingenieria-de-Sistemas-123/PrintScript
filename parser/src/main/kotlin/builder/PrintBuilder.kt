package builder

import node.ASTNode
import node.PrintNode
import helpers.TokenHandler
import org.printscript.token.TokenType

class PrintBuilder(private val h: TokenHandler) {

    fun build(): ASTNode {
        h.expect(TokenType.PRINTLN, "Se esperaba 'println'")
        h.expect(TokenType.OPEN_PAREN, "Se esperaba '('")
        val expr = ExpressionBuilder(h).build()
        h.expect(TokenType.CLOSE_PAREN, "Se esperaba ')'")
        h.expect(TokenType.SEMICOLON, "Se esperaba ';'")
        return PrintNode(expr)
    }
}