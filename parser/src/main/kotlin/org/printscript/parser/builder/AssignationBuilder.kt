package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class AssignationBuilder(private val line: List<Token>) : Builder {
    override fun build(): ASTNode {
        val handler = TokenHandler(line)
        val identifier = handler.consume(TokenType.IDENTIFIER, "Se esperaba el nombre de la variable.")
        val name = identifier.value
        val position = Position(identifier.line, identifier.column)

        handler.consume(TokenType.EQUAL, "Se esperaba '=' después del nombre de la variable.")

        val exprTokens = handler.collectExpressionTokens(false)

        val semi = handler.consume(TokenType.SYNTAX, "Se esperaba ';' después de la declaración.")
        if (semi.value != ";") throw ParseException("Se esperaba ';' pero encontré '${semi.value}'", semi.line, semi.column)

        return AssignationNode(name, ExpressionBuilder(exprTokens).build(), position)
    }
}
