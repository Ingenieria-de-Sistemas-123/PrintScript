package org.printscript.parser


import org.printscript.parser.helpers.ASTGenerator
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.token.Token
import org.printscript.token.TokenType


class DefaultParser : Parser {

    override fun parse(tokens: List<Token>): List<ASTNode> {
        val handler = TokenHandler(tokens)
        val nodes = mutableListOf<ASTNode>()
        val generator = ASTGenerator()

        while (!handler.isAtEnd() && handler.peek().type != TokenType.EOF) {
            val statementTokens = collectStatement(handler)
            nodes += generator.createAST(statementTokens)
        }
        return nodes
    }

    private fun collectStatement(handler: TokenHandler): List<Token> {
        val statement = mutableListOf<Token>()

        fun take(): Token {
            val t = handler.peek()
            handler.advance()
            statement += t
            return t
        }

        val first = take()

        if (first.type == TokenType.IF) {
            if (handler.isAtEnd()) {
                throw ParseException("Se esperaba '(' después de 'if'", first.line, first.column)
            }
            val openParen = take()
            if (!(openParen.type == TokenType.SYNTAX && openParen.value == "(")) {
                throw ParseException("Se esperaba '(' pero encontré '${openParen.value}'", openParen.line, openParen.column)
            }

            var parenDepth = 1
            while (!handler.isAtEnd() && parenDepth > 0) {
                val t = take()
                if (t.type == TokenType.SYNTAX && t.value == "(") parenDepth++
                if (t.type == TokenType.SYNTAX && t.value == ")") parenDepth--
            }

            fun consumeBlock() {
                if (handler.isAtEnd()) throw ParseException(
                    "Se esperaba '{' después de la condición",
                    first.line,
                    first.column
                )
                val open = take()
                if (!(open.type == TokenType.SYNTAX && open.value == "{")) {
                    throw ParseException("Se esperaba '{' pero encontré '${open.value}'", open.line, open.column)
                }
                var braceDepth = 1
                while (!handler.isAtEnd() && braceDepth > 0) {
                    val t = take()
                    if (t.type == TokenType.SYNTAX && t.value == "{") braceDepth++
                    if (t.type == TokenType.SYNTAX && t.value == "}") braceDepth--
                }
                if (braceDepth != 0) {
                    throw ParseException("Bloque '{...}' incompleto en 'if'", open.line, open.column)
                }
            }

            consumeBlock()
            if (!handler.isAtEnd() && handler.peek().type == TokenType.ELSE) {
                take()
                consumeBlock()
            }
            return statement
        }

        while (!handler.isAtEnd()) {
            val t = take()
            if (t.type == TokenType.SYNTAX && t.value == ";") break
        }
        return statement
    }
}