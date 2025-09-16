package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.ReadEnvNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class ReadEnvBuilder(private val line: List<Token>) : Builder {
    override fun build(): ASTNode {
        val handler = TokenHandler(line)
        val readEnv = handler.consume(TokenType.READ_ENV, "Se esperaba 'readEnv' al principio de la expresión.")
        val position = Position(readEnv.line, readEnv.column)

        val lp = handler.consume(TokenType.SYNTAX, "Se esperaba '(' después de 'readEnv'.")
        if (lp.value != "(") throw ParseException("Se esperaba '(' pero encontré '${lp.value}'", lp.line, lp.column)

        val inner = handler.collectExpressionTokensInParenthesis()

        return ReadEnvNode(ExpressionBuilder(inner).build(), position)
    }
}
