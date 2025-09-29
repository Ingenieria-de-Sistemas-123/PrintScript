package org.printscript.parser.builder

import org.printscript.common.Position
import org.printscript.parser.ParseException
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
        if (tokens.isEmpty()) {
            throw ParseException("Expresión vacía o incompleta", 0, 0)
        }
        if (tokens.size == 1) return literalFrom(tokens.first())

        if (tokens.first().value == "(" && tokens.last().value == ")") {
            // verificar que el paréntesis inicial cierra exactamente en el último índice
            var depth = 0
            var closesAtEnd = false
            for (i in tokens.indices) {
                val v = tokens[i].value
                if (v == "(") {
                    depth++
                } else if (v == ")") {
                    depth--
                }
                if (depth == 0) {
                    closesAtEnd = (i == tokens.lastIndex)
                    break
                }
            }
            if (closesAtEnd) {
                return addNodes(tokens.subList(1, tokens.size - 1))
            }
        }

        // detectar llamada a readEnv / readInput como expresión primaria
        if (tokens.size >= 3 &&
            (tokens.first().type == TokenType.READ_ENV || tokens.first().type == TokenType.READ_INPUT) &&
            tokens[1].value == "(" && tokens.last().value == ")"
        ) {
            return when (tokens.first().type) {
                TokenType.READ_ENV -> ReadEnvBuilder(tokens).build()
                TokenType.READ_INPUT -> ReadInputBuilder(tokens).build()
                else -> error("Caso imposible por guard previo")
            }
        }

        var bestIndex = -1
        var bestPrec = Int.MAX_VALUE
        var depth = 0
        for (i in tokens.indices) {
            val tok = tokens[i]
            if (tok.value == "(") {
                depth++
                continue
            }
            if (tok.value == ")") {
                depth--
                continue
            }
            if (depth == 0 && tok.value in listOf("+", "-", "*", "/")) {
                val prec = getPrecedence(tok.value)
                if (prec <= bestPrec) { // asociatividad izquierda: último de misma precedencia
                    bestPrec = prec
                    bestIndex = i
                }
            }
        }

        if (bestIndex == -1) {
            // Puede ser un caso no soportado por ExpressionBuilder (ej: llamada a función) o sintaxis inválida
            throw ParseException("No se encontró operador en la expresión", tokens.first().line, tokens.first().column)
        }

        val operator = tokens[bestIndex]
        val leftTokens = tokens.subList(0, bestIndex)
        val rightTokens = tokens.subList(bestIndex + 1, tokens.size)

        if (rightTokens.isEmpty()) {
            throw ParseException("Falta operando a la derecha del operador '${operator.value}'", operator.line, operator.column)
        }
        val rightNode = addNodes(rightTokens)

        if (leftTokens.isEmpty() && operator.value == "-") {
            val pos = Position(operator.line, operator.column)
            return DoubleExpressionNode(LiteralNode(0, TokenType.NUMBER), "-", rightNode, pos)
        }
        if (leftTokens.isEmpty()) {
            throw ParseException("Falta operando a la izquierda del operador '${operator.value}'", operator.line, operator.column)
        }
        val leftNode = addNodes(leftTokens)
        val pos = Position(operator.line, operator.column)
        return DoubleExpressionNode(leftNode, operator.value, rightNode, pos)
    }

    private fun getPrecedence(op: String): Int =
        when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> Int.MAX_VALUE
        }

    private fun literalFrom(token: Token): LiteralNode<*> =
        when (token.type) {
            TokenType.NUMBER -> {
                try {
                    val doubleValue = token.value.toDouble()
                    LiteralNode(doubleValue, TokenType.NUMBER)
                } catch (e: NumberFormatException) {
                    throw ParseException(
                        "Invalid number format for token value: '${token.value}'",
                        token.line,
                        token.column,
                    )
                }
            }
            TokenType.STRING -> LiteralNode(unescapeString(token.value), TokenType.STRING)
            TokenType.TRUE -> LiteralNode(true, TokenType.TRUE)
            TokenType.FALSE -> LiteralNode(false, TokenType.FALSE)
            TokenType.IDENTIFIER -> LiteralNode(token.value, TokenType.IDENTIFIER)
            TokenType.READ_ENV -> LiteralNode(token.value, TokenType.READ_ENV)
            TokenType.SYNTAX -> throw ParseException("Token de sintaxis inesperado: ${token.value}", token.line, token.column)
            else -> throw ParseException("Token inesperado en literal: ${token.type}", token.line, token.column)
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
