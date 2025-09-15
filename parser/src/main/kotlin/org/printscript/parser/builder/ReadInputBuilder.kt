package org.printscript.parser.builder

import com.printscript.parser.builder.ExpressionBuilder
import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class ReadInputBuilder(private val line: List<Token>) : Builder {
    override fun build(): ASTNode {
        val handler = TokenHandler(line)
        val readInput = handler.consume(TokenType.READ_INPUT, "Se esperaba 'readInput' al principio de la expresión.")
        val position = Position(readInput.line, readInput.column)

        val lp = handler.consume(TokenType.SYNTAX, "Se esperaba '(' después de 'readInput'.")
        if (lp.value != "(") throw ParseException("Se esperaba '(' pero encontré '${lp.value}'", lp.line, lp.column)

        val inner = handler.collectExpressionTokensInParenthesis()

        return ReadInputNode(ExpressionBuilder(inner).build(), position)
    }
}