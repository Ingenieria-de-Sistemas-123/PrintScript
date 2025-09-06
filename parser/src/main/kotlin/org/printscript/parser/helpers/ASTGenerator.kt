package org.printscript.parser.helpers

import org.printscript.parser.ParseException
import org.printscript.parser.builder.AssignationBuilder
import org.printscript.parser.builder.PrintBuilder
import org.printscript.parser.builder.VariableDeclarationBuilder
import org.printscript.parser.node.ASTNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class ASTGenerator {
    fun createAST(line: List<Token>): ASTNode {
        require(line.isNotEmpty()) { "Línea vacía" }
        val first = line.first()
        val h = TokenHandler(line)

        return when (first.type) {
            TokenType.LET -> VariableDeclarationBuilder(h).build()
            TokenType.PRINTLN -> PrintBuilder(h).build()
            TokenType.IDENTIFIER -> {
                if (line.getOrNull(1)?.type == TokenType.EQUAL) {
                    AssignationBuilder(h).build()
                } else {
                    throw ParseException(
                        "Se esperaba '=' después del identificador",
                        first.line,
                        first.column,
                    )
                }
            }
            else -> throw ParseException(
                "Inicio de sentencia inesperado: ${first.type}",
                first.line,
                first.column,
            )
        }
    }
}
