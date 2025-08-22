package builder

import com.printscript.parser.ParseException
import node.ASTNode
import node.DeclarationNode
import node.LiteralNode
import helpers.TokenHandler
import org.printscript.token.TokenType

class VariableDeclarationBuilder(private val h: TokenHandler) {

    fun build(): ASTNode {
        h.expect(TokenType.LET, "Se esperaba 'let'")
        val name = h.expect(TokenType.IDENTIFIER, "Se esperaba el nombre de la variable").value
        h.expect(TokenType.COLON, "Se esperaba ':' después del nombre")

        val typeTok = h.current()
        val type = when {
            h.match(TokenType.NUMBER_TYPE) -> "number"
            h.match(TokenType.STRING_TYPE) -> "string"
            else -> throw ParseException("Se esperaba el tipo (number|string)", typeTok.line, typeTok.column)
        }

        val value: ASTNode =
            if (h.match(TokenType.EQUAL)) ExpressionBuilder(h).build()
            else LiteralNode("empty", type)

        h.expect(TokenType.SEMICOLON, "Se esperaba ';' al final de la declaración")
        return DeclarationNode(name, type, value)
    }
}