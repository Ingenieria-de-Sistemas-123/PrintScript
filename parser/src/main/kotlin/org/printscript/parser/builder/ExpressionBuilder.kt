package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.token.Token
import org.printscript.token.TokenType

class ExpressionBuilder(private val line: List<Token>) : Builder {
    override fun build(): ASTNode {
        // eliminar tokens EOF que puedan venir en la lista para no romper la lógica
        val tokens = line.filter { it.type != TokenType.EOF }
        return if (tokens.isEmpty()) {
            // representar expresión vacía como literal string vacío
            LiteralNode("", TokenType.STRING)
        } else {
            addNodes(tokens)
        }
    }

    private fun addNodes(tokens: List<Token>): ASTNode {
        if (tokens.size == 1) return literalFrom(tokens.first())

        if (tokens.first().value == "(" && tokens.last().value == ")") {
            return addNodes(tokens.subList(1, tokens.size - 1))
        }

        val operators = mutableListOf<Pair<Token, Int>>()
        tokens.forEachIndexed { idx, tok -> if (operatorsToCheck(tok)) operators += tok to idx }

        val (operator, index) = findLowestPrecedenceOperator(operators)

        val leftTokens = tokens.subList(0, index)
        val rightTokens = tokens.subList(index + 1, tokens.size)

        val rightNode = addNodes(rightTokens)

        if (leftTokens.isEmpty() && operator.value == "-") {
            val pos = Position(operator.line, operator.column)
            return DoubleExpressionNode(LiteralNode(0, TokenType.NUMBER), "-", rightNode, pos)
        }

        val leftNode = addNodes(leftTokens)
        val pos = Position(operator.line, operator.column)
        return DoubleExpressionNode(leftNode, operator.value, rightNode, pos)
    }

    private fun findLowestPrecedenceOperator(operators: List<Pair<Token, Int>>): Pair<Token, Int> {
        if (operators.isEmpty()) {
            throw IllegalStateException("No se encontró operador en la expresión")
        }

        var result: Pair<Token, Int>? = null
        var i = 0
        var parenCount = 0

        while (i < operators.size) {
            val (token, index) = operators[i]

            if (token.value == "(") {
                parenCount++
            } else if (token.value == ")") {
                parenCount--
            } else if (parenCount == 0) {
                if (result == null || getPrecedence(token.value) <= getPrecedence(result.first.value)) {
                    result = token to index
                }
            }
            i++
        }

        return result!!
    }

    private fun getPrecedence(op: String): Int =
        when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> Int.MAX_VALUE
        }

    private fun operatorsToCheck(token: Token): Boolean = token.value in listOf("/", "*", "(", ")", "+", "-")

    private fun literalFrom(token: Token): LiteralNode<*> =
        when (token.type) {
            TokenType.NUMBER -> {
                val doubleValue = token.value.toDoubleOrNull()
                if (doubleValue == null) {
                    throw IllegalArgumentException(
                        "Invalid number format for token value: '${token.value}' at line ${token.line}, column ${token.column}",
                    )
                }
                LiteralNode(doubleValue, TokenType.NUMBER)
            }
            TokenType.STRING -> LiteralNode(unescapeString(token.value), TokenType.STRING)
            TokenType.TRUE -> LiteralNode(true, TokenType.TRUE)
            TokenType.FALSE -> LiteralNode(false, TokenType.FALSE)
            TokenType.IDENTIFIER -> LiteralNode(token.value, TokenType.IDENTIFIER)
            else -> LiteralNode(token.value, token.type)
        }

    private fun unescapeString(raw: String): String {
        val withoutQuotes = raw.removeSurrounding("\"")
        val sb = StringBuilder()
        var i = 0
        while (i < withoutQuotes.length) {
            val c = withoutQuotes[i]
            if (c == '\\' && i + 1 < withoutQuotes.length) {
                when (val next = withoutQuotes[i + 1]) {
                    '\\' -> sb.append('\\')
                    '"' -> sb.append('"')
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    'r' -> sb.append('\r')
                    else -> sb.append(next)
                }
                i += 2
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }
}
