package org.printscript.formatter.emit

import org.printscript.formatter.config.BraceStyle
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.EmptyExpressionNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.ReadEnvNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.token.TokenType

class ASTEmitter(private val cfg: FormatterConfig) {
    fun emitProgram(program: List<ASTNode>): List<FormatToken> {
        val acc = TokenAccumulator()
        emitStatements(program, acc)
        return acc.tokens
    }

    private fun emitStatements(
        statements: List<ASTNode>,
        acc: TokenAccumulator,
    ) {
        statements.forEachIndexed { index, stmt ->
            emitStmt(stmt, acc, index == statements.lastIndex)
        }
    }

    private fun emitStmt(
        node: ASTNode,
        acc: TokenAccumulator,
        isLastInBlock: Boolean,
    ) {
        when (node) {
            is DeclarationNode -> emitDeclaration(node, acc)
            is AssignationNode -> emitAssignation(node, acc)
            is PrintStatementNode -> emitPrint(node, acc)
            is IfElseNode -> emitIfElse(node, acc, isLastInBlock)
            else -> error("Sentencia no soportada por el formatter: ${node::class.simpleName}")
        }
    }

    private fun emitDeclaration(
        node: DeclarationNode,
        acc: TokenAccumulator,
    ) {
        acc.addKeyword("let")
        acc.add(FormatToken.Ident(node.identifier))
        acc.add(FormatToken.Colon)
        acc.add(FormatToken.TypeName(node.valueType))
        if (node.expression !== EmptyExpressionNode) {
            acc.add(FormatToken.Equals)
            emitExpr(node.expression, acc)
        }
        acc.add(FormatToken.Semicolon)
    }

    private fun emitAssignation(
        node: AssignationNode,
        acc: TokenAccumulator,
    ) {
        acc.add(FormatToken.Ident(node.variable))
        acc.add(FormatToken.Equals)
        emitExpr(node.expression, acc)
        acc.add(FormatToken.Semicolon)
    }

    private fun emitPrint(
        node: PrintStatementNode,
        acc: TokenAccumulator,
    ) {
        acc.newline(cfg.lineJumpBeforePrintln)
        acc.addKeyword("println")
        acc.add(FormatToken.OpenParen)
        emitExpr(node.expression, acc)
        acc.add(FormatToken.CloseParen)
        acc.add(FormatToken.Semicolon)
    }

    private fun emitIfElse(
        node: IfElseNode,
        acc: TokenAccumulator,
        isLastInBlock: Boolean,
    ) {
        acc.addKeyword("if")
        acc.addSpace()
        acc.add(FormatToken.OpenParen)
        emitExpr(node.condition, acc)
        acc.add(FormatToken.CloseParen)

        appendOpenBrace(acc)
        acc.withIndent { emitStatements(node.ifBranch, acc) }
        ensureBlockClosingLine(node.ifBranch, acc)
        acc.add(FormatToken.CloseBrace)

        if (node.elseBranch.isNotEmpty()) {
            if (cfg.braceStyle == BraceStyle.SAME_LINE) {
                acc.addSpace()
                acc.addKeyword("else")
            } else {
                acc.newline()
                acc.addKeyword("else")
            }
            appendOpenBrace(acc)
            acc.withIndent { emitStatements(node.elseBranch, acc) }
            ensureBlockClosingLine(node.elseBranch, acc)
            acc.add(FormatToken.CloseBrace)
        }

        if (!isLastInBlock) {
            acc.newline()
        } else {
            acc.markLineStart()
        }
    }

    private fun appendOpenBrace(acc: TokenAccumulator) {
        when (cfg.braceStyle) {
            BraceStyle.SAME_LINE -> {
                acc.addSpace()
                acc.add(FormatToken.OpenBrace)
                acc.newline()
            }

            BraceStyle.NEXT_LINE -> {
                acc.newline()
                acc.add(FormatToken.OpenBrace)
                acc.newline()
            }
        }
    }

    private fun ensureBlockClosingLine(
        statements: List<ASTNode>,
        acc: TokenAccumulator,
    ) {
        if (acc.isAtLineStart()) {
            return
        }

        if (statements.isEmpty()) {
            acc.markLineStart()
            return
        }

        if (!cfg.lineJumpAfterSemicolon || statements.last() is IfElseNode) {
            acc.newline()
        }
    }

    private fun emitExpr(
        node: ASTNode,
        acc: TokenAccumulator,
    ) {
        when (node) {
            is DoubleExpressionNode -> {
                emitExpr(node.left, acc)
                acc.add(
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
                emitExpr(node.right, acc)
            }

            is LiteralNode<*> -> emitLiteral(node, acc)
            is ReadInputNode -> emitReadCall("readInput", node.expression, acc)
            is ReadEnvNode -> emitReadCall("readEnv", node.expression, acc)
            else -> error("Expresión no soportada por el formatter: ${node::class.simpleName}")
        }
    }

    private fun emitReadCall(
        name: String,
        argument: ASTNode,
        acc: TokenAccumulator,
    ) {
        acc.addKeyword(name)
        acc.add(FormatToken.OpenParen)
        emitExpr(argument, acc)
        acc.add(FormatToken.CloseParen)
    }

    private fun emitLiteral(
        node: LiteralNode<*>,
        acc: TokenAccumulator,
    ) {
        val tokenType = node.tokenType
        when (tokenType) {
            TokenType.IDENTIFIER -> {
                acc.add(FormatToken.Ident(node.value.toString()))
                return
            }

            TokenType.TRUE, TokenType.FALSE -> {
                acc.add(FormatToken.Ident(node.value.toString()))
                return
            }

            TokenType.NUMBER -> {
                acc.add(FormatToken.NumberLit(formatNumber(node.value.toString())))
                return
            }

            TokenType.STRING -> {
                val normalized = unquoteAndUnescape(node.value.toString())
                acc.add(FormatToken.StringLit(normalized))
                return
            }

            else -> {}
        }

        when (val value = node.value) {
            is Number -> acc.add(FormatToken.NumberLit(formatNumber(value.toString())))
            is Boolean -> acc.add(FormatToken.Ident(value.toString()))
            is String -> {
                val normalized = unquoteAndUnescape(value)
                acc.add(FormatToken.StringLit(normalized))
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

    private inner class TokenAccumulator {
        val tokens: MutableList<FormatToken> = mutableListOf()
        private var indentLevel = 0
        private var atLineStart = true

        fun add(token: FormatToken) {
            when (token) {
                is FormatToken.NewLine -> {
                    tokens += token
                    atLineStart = true
                    return
                }

                is FormatToken.Space -> {
                    if (!atLineStart) {
                        tokens += token
                    }
                    return
                }

                is FormatToken.Indent -> {
                    tokens += token
                    atLineStart = false
                    return
                }

                else -> {
                    ensureIndent()
                    tokens += token
                    atLineStart = token is FormatToken.Semicolon && cfg.lineJumpAfterSemicolon
                }
            }
        }

        fun addKeyword(text: String) {
            add(FormatToken.Keyword(text))
        }

        fun addSpace() {
            add(FormatToken.Space)
        }

        fun newline(times: Int = 1) {
            if (times <= 0) {
                return
            }
            tokens += FormatToken.NewLine(times)
            atLineStart = true
        }

        fun markLineStart() {
            atLineStart = true
        }

        fun isAtLineStart(): Boolean = atLineStart

        fun withIndent(block: () -> Unit) {
            indentLevel++
            block()
            indentLevel--
        }

        private fun ensureIndent() {
            if (!atLineStart) {
                return
            }
            if (indentLevel > 0) {
                tokens += FormatToken.Indent(cfg.indentSize * indentLevel)
            }
            atLineStart = false
        }
    }
}
