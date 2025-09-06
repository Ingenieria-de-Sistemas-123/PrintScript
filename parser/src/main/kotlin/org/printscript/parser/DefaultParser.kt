package org.printscript.parser

import org.printscript.parser.builder.AssignationBuilder
import org.printscript.parser.builder.PrintBuilder
import org.printscript.parser.builder.VariableDeclarationBuilder
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class DefaultParser : Parser {
    override fun parse(list: List<Token>): List<ASTNode> {
        val h = TokenHandler(list)
        val out = mutableListOf<ASTNode>()

        while (!h.atEnd()) {
            val node =
                when (h.current().type) {
                    TokenType.LET -> VariableDeclarationBuilder(h).build()
                    TokenType.PRINTLN -> PrintBuilder(h).build()
                    TokenType.IDENTIFIER ->
                        if (list.getOrNull(h.pos + 1)?.type == TokenType.EQUAL) {
                            AssignationBuilder(h).build()
                        } else {
                            val t = h.current()
                            throw ParseException("Se esperaba una asignación o declaración", t.line, t.column)
                        }
                    else -> {
                        val t = h.current()
                        throw ParseException("Token inesperado en inicio de sentencia: ${t.type}", t.line, t.column)
                    }
                }
            out += node
        }
        return out
    }
}
