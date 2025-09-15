package com.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.builder.Builder
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.LiteralNode
import org.printscript.token.Token
import org.printscript.token.TokenType
import org.printscript.parser.node.DoubleExpressionNode


class ExpressionBuilder(private val line: List<Token>) : Builder {

    override fun build(): ASTNode {
        return if (line.isEmpty()) LiteralNode("\"\"") else addNodes(line)
    }

    private fun addNodes(tokens: List<Token>): ASTNode {
        if (tokens.size == 1) {
            val v = tokens[0].value
            @Suppress("SwallowedException", "TooGenericExceptionCaught")
            return try {
                LiteralNode(v.toInt())
            } catch (_: NumberFormatException) {
                try {
                    LiteralNode(v.toDouble())
                } catch (_: NumberFormatException) {
                    try {
                        LiteralNode(v.toBooleanStrict())
                    } catch (_: Exception) {
                        LiteralNode(v)
                    }
                }
            }
        }

        if (tokens.first().value == "(" && tokens.last().value == ")") {
            return addNodes(tokens.subList(1, tokens.size - 1))
        }

        val operators = mutableListOf<Pair<Token, Int>>()
        tokens.forEachIndexed { idx, tok -> if (operatorsToCheck(tok)) operators += tok to idx }

        val (operator, index) = findLowestPrecedenceOperator(operators)

        val leftTokens  = tokens.subList(0, index)
        val rightTokens = tokens.subList(index + 1, tokens.size)

        val rightNode = addNodes(rightTokens)

        if (leftTokens.isEmpty() && operator.value == "-") {
            val pos = Position(operator.line, operator.column)
            return DoubleExpressionNode(LiteralNode(0), "-", rightNode, pos)
        }

        val leftNode = addNodes(leftTokens)
        val pos = Position(operator.line, operator.column)
        return DoubleExpressionNode(leftNode, operator.value, rightNode, pos)
    }

    private fun findLowestPrecedenceOperator(operators: List<Pair<Token, Int>>): Pair<Token, Int> {
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
                if (result == null || getPrecedence(token.value) <= getPrecedence(result!!.first.value)) {
                    result = token to index
                }
            }
            i++
        }

        return result ?: operators[0]
    }

    private fun getPrecedence(op: String): Int = when (op) {
        "+", "-" -> 1
        "*", "/" -> 2
        else     -> Int.MAX_VALUE
    }

    private fun operatorsToCheck(token: Token): Boolean =
        token.value in listOf("/", "*", "(", ")", "+", "-")
}