package helpers

import builder.AssignationBuilder
import builder.PrintBuilder
import builder.VariableDeclarationBuilder
import com.printscript.parser.ParseException
import node.ASTNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class ASTGenerator {

    fun createAST(line: List<Token>): ASTNode {
        require(line.isNotEmpty()) { "Línea vacía" }
        val first = line.first()

        return when (first.type) {
            TokenType.LET -> VariableDeclarationBuilder(TokenHandler(line)).build()
            TokenType.PRINTLN -> PrintBuilder(TokenHandler(line)).build()
            TokenType.IDENTIFIER -> {
                // Asignación: id = expr ;
                if (line.getOrNull(1)?.type == TokenType.EQUAL) {
                    AssignationBuilder(TokenHandler(line)).build()
                } else {
                    throw ParseException(
                        "Se esperaba '=' después del identificador",
                        first.line,
                        first.column
                    )
                }
            }
            else -> throw ParseException("Inicio de sentencia inesperado: ${first.type}", first.line, first.column)
        }
    }
}