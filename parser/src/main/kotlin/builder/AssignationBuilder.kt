package builder

import node.ASTNode
import node.AssignationNode
import helpers.TokenHandler
import org.printscript.token.TokenType

class AssignationBuilder(private val h: TokenHandler) {

    fun build(): ASTNode {
        val name = h.expect(TokenType.IDENTIFIER, "Se esperaba el nombre de la variable").value
        h.expect(TokenType.EQUAL, "Se esperaba '=' después del nombre")
        val value = ExpressionBuilder(h).build()
        h.expect(TokenType.SEMICOLON, "Se esperaba ';' al final de la asignación")
        return AssignationNode(name, value)
    }
}