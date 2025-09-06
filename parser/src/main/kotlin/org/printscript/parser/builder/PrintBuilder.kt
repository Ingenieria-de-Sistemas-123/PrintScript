package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.PrintNode
import org.printscript.token.TokenType

class PrintBuilder(private val h: TokenHandler) {
    fun build(): ASTNode {
        val print = h.expect(TokenType.PRINTLN, "Se esperaba 'println'")
        val pos = Position(print.line, print.column)

        h.expect(TokenType.OPEN_PAREN, "Se esperaba '('")
        val expr = ExpressionBuilder(h).build()
        h.expect(TokenType.CLOSE_PAREN, "Se esperaba ')'")
        h.expect(TokenType.SEMICOLON, "Se esperaba ';'")

        return PrintNode(expr, pos)
    }
}
