package org.printscript.formatter.emit

import org.printscript.formatter.rules.FormatToken
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.ReadEnvNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.token.TokenType

/**
 * Emite los tokens correspondientes a expresiones, delegando el control de
 * indentación y layout al [TokenBuffer].
 */
class ExpressionEmitter {
    fun emit(
        node: ASTNode,
        buffer: TokenBuffer,
    ) {
        when (node) {
            is DoubleExpressionNode -> emitDoubleExpression(node, buffer)
            is LiteralNode<*> -> emitLiteral(node, buffer)
            is ReadInputNode -> emitReadCall("readInput", node.expression, buffer)
            is ReadEnvNode -> emitReadCall("readEnv", node.expression, buffer)
            else -> error("Expresión no soportada por el formatter: ${node::class.simpleName}")
        }
    }

    private fun emitDoubleExpression(
        node: DoubleExpressionNode,
        buffer: TokenBuffer,
    ) {
        emit(node.left, buffer)
        buffer.add(
            FormatToken.Op(
                when (node.operator.trim()) {
                    "+" -> FormatToken.OpKind.PLUS
                    "-" -> FormatToken.OpKind.MINUS
                    "*" -> FormatToken.OpKind.STAR
                    "/" -> FormatToken.OpKind.SLASH
                    else -> error("Operador no soportado: '${node.operator}'")
                },
            ),
        )
        emit(node.right, buffer)
    }

    private fun emitReadCall(
        name: String,
        argument: ASTNode,
        buffer: TokenBuffer,
    ) {
        buffer.addKeyword(name)
        buffer.add(FormatToken.OpenParen)
        emit(argument, buffer)
        buffer.add(FormatToken.CloseParen)
    }

    private fun emitLiteral(
        node: LiteralNode<*>,
        buffer: TokenBuffer,
    ) {
        val tokenType = node.tokenType
        when (tokenType) {
            TokenType.IDENTIFIER, TokenType.TRUE, TokenType.FALSE -> {
                buffer.add(FormatToken.Ident(node.value.toString()))
                return
            }

            TokenType.NUMBER -> {
                buffer.add(FormatToken.NumberLit(formatNumber(node.value.toString())))
                return
            }

            TokenType.STRING -> {
                val normalized = unquoteAndUnescape(node.value.toString())
                buffer.add(FormatToken.StringLit(normalized))
                return
            }

            else -> {
                // Fall-through para manejar valores construidos directamente en el AST
            }
        }

        when (val value = node.value) {
            is Number -> buffer.add(FormatToken.NumberLit(formatNumber(value.toString())))
            is Boolean -> buffer.add(FormatToken.Ident(value.toString()))
            is String -> {
                val normalized = unquoteAndUnescape(value)
                buffer.add(FormatToken.StringLit(normalized))
            }

            else ->
                error(
                    "Literal no soportado: value='$value' (${value?.let { it::class.simpleName }})",
                )
        }
    }

    private fun unquoteAndUnescape(v: String): String {
        val inner =
            if (v.length >= 2 && v.first() == '"' && v.last() == '"') {
                v.substring(1, v.length - 1)
            } else {
                v
            }
        return unescape(inner)
    }

    private fun unescape(s: String): String =
        buildString {
            var i = 0
            while (i < s.length) {
                val c = s[i]
                if (c == '\\' && i + 1 < s.length) {
                    when (s[i + 1]) {
                        '\\' -> {
                            append('\\')
                            i++
                        }

                        '"' -> {
                            append('"')
                            i++
                        }

                        'n' -> {
                            append('\n')
                            i++
                        }

                        't' -> {
                            append('\t')
                            i++
                        }

                        'r' -> {
                            append('\r')
                            i++
                        }

                        else -> {
                            append(s[i + 1])
                            i++
                        }
                    }
                } else {
                    append(c)
                }
                i++
            }
        }

    private fun formatNumber(raw: String): String = raw.removeSuffix(".0")
}
