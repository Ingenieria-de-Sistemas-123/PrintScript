package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class PrintStatementBuilder(private val line: List<Token>) : Builder {
    override fun build(): ASTNode {
        val handler = TokenHandler(line)
        val print = handler.consume(TokenType.PRINTLN, "Se esperaba 'println' al principio de la declaración.")
        val position = Position(print.line, print.column)

        val lp = handler.consume(TokenType.SYNTAX, "Se esperaba '(' después de 'println'.")
        if (lp.value != "(") throw ParseException("Se esperaba '(' pero encontré '${lp.value}'", lp.line, lp.column)

        val inner = handler.collectExpressionTokensInParenthesis()

        val expresion =
            when (inner.firstOrNull()?.type) {
                TokenType.READ_ENV -> ReadEnvBuilder(inner).build()
                TokenType.READ_INPUT -> ReadInputBuilder(inner).build()
                else -> ExpressionBuilder(inner).build()
            }

        val semicolon = handler.consume(TokenType.SYNTAX, "Se esperaba ';' después de la declaración.")
        if (semicolon.value != ";") {
            throw ParseException(
                "Se esperaba ';' pero encontré '${semicolon.value}'",
                semicolon.line,
                semicolon.column,
            )
        }

        return PrintStatementNode(expresion, position)
    }
}
