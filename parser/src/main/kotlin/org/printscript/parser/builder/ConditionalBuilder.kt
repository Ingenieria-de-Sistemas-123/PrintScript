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

        return IfElseNode(thenBranch, elseBranch, LiteralNode(cond, TokenType.IDENTIFIER))
    }

    private fun parseCondition(h: TokenHandler): String {
        val lp = h.peek()
        if (lp.type != TokenType.SYNTAX || lp.value != "(") {
            throw ParseException("Se esperaba '('", lp.line, lp.column)
        }
        h.advance()

        if (h.peek().type != TokenType.IDENTIFIER) {
            val t = h.peek()
            throw ParseException("La condición de 'if' debe ser una variable booleana", t.line, t.column)
        }
        val ident = h.consume(TokenType.IDENTIFIER, "Se esperaba un identificador").value

        val rp = h.peek()
        if (rp.type != TokenType.SYNTAX || rp.value != ")") {
            throw ParseException("Se esperaba ')'", rp.line, rp.column)
        }
        h.advance()

        return ident
    }

    private fun parseBlock(h: TokenHandler): List<ASTNode> {
        val lb = h.peek()
        if (lb.type != TokenType.SYNTAX || lb.value != "{") {
            throw ParseException("Se esperaba '{'", lb.line, lb.column)
        }
        h.advance()

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

        val rb = h.peek()
        if (rb.type != TokenType.SYNTAX || rb.value != "}") {
            throw ParseException("Se esperaba '}'", rb.line, rb.column)
        }
        h.advance()
        return out
    }
}
