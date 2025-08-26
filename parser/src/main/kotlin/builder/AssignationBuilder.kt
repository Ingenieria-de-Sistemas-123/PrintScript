package builder

import node.ASTNode
import node.AssignationNode
import helpers.TokenHandler
import org.printscript.token.TokenType
import org.printscript.common.Position

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