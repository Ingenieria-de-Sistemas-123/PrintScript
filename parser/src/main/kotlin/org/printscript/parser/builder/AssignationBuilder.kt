package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.token.TokenType

class AssignationBuilder(private val h: TokenHandler) {
    fun build(): ASTNode {
        val id = h.expect(TokenType.IDENTIFIER, "Se esperaba el nombre de la variable")
        val name = id.value
        val pos = Position(id.line, id.column)

        h.expect(TokenType.EQUAL, "Se esperaba '=' después del nombre")
        val value = ExpressionBuilder(h).build()
        h.expect(TokenType.SEMICOLON, "Se esperaba ';' al final de la asignación")

        return AssignationNode(name, value, pos)
    }
}
