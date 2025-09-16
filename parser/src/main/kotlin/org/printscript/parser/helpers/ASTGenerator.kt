package org.printscript.parser.helpers

import org.printscript.parser.ParseException
import org.printscript.parser.builder.AssignationBuilder
import org.printscript.parser.builder.ConditionalBuilder
import org.printscript.parser.builder.PrintStatementBuilder
import org.printscript.parser.builder.VariableDeclarationBuilder
import org.printscript.parser.node.ASTNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class ASTGenerator {
    private val dispatch: Map<TokenType, (List<Token>) -> ASTNode> =
        mapOf(
            TokenType.LET to { tokens -> VariableDeclarationBuilder(tokens).build() },
            TokenType.CONST to { tokens -> VariableDeclarationBuilder(tokens).build() },
            TokenType.IF to { tokens -> ConditionalBuilder(tokens).build() },
            TokenType.PRINTLN to { tokens -> PrintStatementBuilder(tokens).build() },
            TokenType.IDENTIFIER to { tokens -> AssignationBuilder(tokens).build() },
        )

    fun createAST(line: List<Token>): ASTNode {
        require(line.isNotEmpty()) { "Línea vacía" }
        val first = line.first()
        return dispatch[first.type]?.invoke(line)
            ?: throw ParseException(
                "Token inesperado en inicio de sentencia: ${first.type}",
                first.line,
                first.column,
            )
    }
}
