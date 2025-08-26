package builder

import com.printscript.parser.ParseException
import node.ASTNode
import node.DeclarationNode
import node.LiteralNode
import helpers.TokenHandler
import org.printscript.common.Position
import org.printscript.token.TokenType

class VariableDeclarationBuilder(private val h: TokenHandler) {

    fun build(): ASTNode {
        h.expect(TokenType.LET, "Se esperaba 'let'")
        val id = h.expect(TokenType.IDENTIFIER, "Se esperaba el nombre de la variable")
        val name = id.value
        val pos = Position(id.line, id.column)

        h.expect(TokenType.COLON, "Se esperaba ':' después del nombre")

        val typeTok = h.current()
        val type = when {
            h.match(TokenType.NUMBER_TYPE) -> "number"
            h.match(TokenType.STRING_TYPE) -> "string"
            else -> throw ParseException("Se esperaba el tipo (number|string)", typeTok.line, typeTok.column)
        }

        val value: ASTNode =
            if (h.match(TokenType.EQUAL)) ExpressionBuilder(h).build()
            else LiteralNode("empty", type, pos)

        h.expect(TokenType.SEMICOLON, "Se esperaba ';' al final de la declaración")
        return DeclarationNode(name, type, value, pos)
    }
}