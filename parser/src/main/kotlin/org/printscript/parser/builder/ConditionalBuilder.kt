package org.printscript.parser.builder

import org.printscript.parser.ParseException
import org.printscript.parser.helpers.ASTGenerator
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.LiteralNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class ConditionalBuilder(private val line: List<Token>) : Builder {
    override fun build(): ASTNode {
        val handler = TokenHandler(line)
        val tokenIf = handler.consume(TokenType.IF, "Se esperaba 'if'")
        val cond = parseCondition(handler)
        val thenBranch = parseBlock(handler)
        val elseBranch =
            if (!handler.isAtEnd() && handler.peek().type == TokenType.ELSE) {
                handler.consume(TokenType.ELSE, "Se esperaba 'else'")
                parseBlock(handler)
            } else {
                emptyList()
            }

        return IfElseNode(thenBranch, elseBranch, LiteralNode(cond, TokenType.TRUE))
    }

    private fun parseCondition(h: TokenHandler): String {
        // usa expect para validar tipo y lexema en un único paso
        val lp = h.expect(TokenType.SYNTAX, "(", "Se esperaba '('")

        if (h.peek().type != TokenType.IDENTIFIER) {
            val t = h.peek()
            throw ParseException("La condición de 'if' debe ser una variable booleana", t.line, t.column)
        }
        val ident = h.consume(TokenType.IDENTIFIER, "Se esperaba un identificador").value

        // validar paréntesis de cierre con expect
        h.expect(TokenType.SYNTAX, ")", "Se esperaba ')'")

        return ident
    }

    private fun parseBlock(h: TokenHandler): List<ASTNode> {
        // usar expect para validar '{'
        h.expect(TokenType.SYNTAX, "{", "Se esperaba '{'")

        val out = mutableListOf<ASTNode>()
        val gen = ASTGenerator()

        while (!(h.peek().type == TokenType.SYNTAX && h.peek().value == "}")) {
            val stmt = mutableListOf<Token>()
            var depth = 0

            do {
                val t = h.peek()
                h.advance()
                stmt += t
                if (t.type == TokenType.SYNTAX && (t.value == "(" || t.value == "{")) depth++
                if (t.type == TokenType.SYNTAX && (t.value == ")" || t.value == "}")) depth--
            } while (depth > 0 || (stmt.last().value != ";" && stmt.last().value != "}"))

            out += gen.createAST(stmt)
        }

        // usar expect para '}' también
        h.expect(TokenType.SYNTAX, "}", "Se esperaba '}'")
        return out
    }
}
