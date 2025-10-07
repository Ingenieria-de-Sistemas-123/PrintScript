package org.printscript.formatter.emit

import org.printscript.formatter.config.BraceStyle
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.EmptyExpressionNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.PrintStatementNode

/**
 * Responsable de emitir los tokens correspondientes a las sentencias. Mantiene
 * la lógica específica de layout de bloques (llaves, indentación, saltos de
 * línea) separada de la emisión de expresiones.
 */
class StatementEmitter(
    private val config: FormatterConfig,
    private val expressionEmitter: ExpressionEmitter,
) {
    fun emitProgram(program: List<ASTNode>): List<FormatToken> {
        val buffer = TokenBuffer(config)
        emitStatements(program, buffer)
        return buffer.tokens
    }

    private fun emitStatements(
        statements: List<ASTNode>,
        buffer: TokenBuffer,
    ) {
        statements.forEachIndexed { index, stmt ->
            emitStatement(stmt, buffer, index == statements.lastIndex)
        }
    }

    private fun emitStatement(
        node: ASTNode,
        buffer: TokenBuffer,
        isLastInBlock: Boolean,
    ) {
        when (node) {
            is DeclarationNode -> emitDeclaration(node, buffer)
            is AssignationNode -> emitAssignation(node, buffer)
            is PrintStatementNode -> emitPrint(node, buffer)
            is IfElseNode -> emitIfElse(node, buffer, isLastInBlock)
            else -> error("Sentencia no soportada por el formatter: ${node::class.simpleName}")
        }
    }

    private fun emitDeclaration(
        node: DeclarationNode,
        buffer: TokenBuffer,
    ) {
        buffer.addKeyword("let")
        buffer.add(FormatToken.Ident(node.identifier))
        buffer.add(FormatToken.Colon)
        buffer.add(FormatToken.TypeName(node.valueType))
        if (node.expression !== EmptyExpressionNode) {
            buffer.add(FormatToken.Equals)
            expressionEmitter.emit(node.expression, buffer)
        }
        buffer.add(FormatToken.Semicolon)
    }

    private fun emitAssignation(
        node: AssignationNode,
        buffer: TokenBuffer,
    ) {
        buffer.add(FormatToken.Ident(node.variable))
        buffer.add(FormatToken.Equals)
        expressionEmitter.emit(node.expression, buffer)
        buffer.add(FormatToken.Semicolon)
    }

    private fun emitPrint(
        node: PrintStatementNode,
        buffer: TokenBuffer,
    ) {
        buffer.addKeyword("println")
        buffer.add(FormatToken.OpenParen)
        expressionEmitter.emit(node.expression, buffer)
        buffer.add(FormatToken.CloseParen)
        buffer.add(FormatToken.Semicolon)
        // Now we respect lineJumpAfterSemicolon; totalAfter = (1 if active) + extras
        val base = if (config.lineJumpAfterSemicolon) 1 else 0
        val totalAfter = base + config.printLineBreaksAfter
        if (totalAfter > 0) buffer.newline(totalAfter)
    }

    private fun emitIfElse(
        node: IfElseNode,
        buffer: TokenBuffer,
        isLastInBlock: Boolean,
    ) {
        buffer.addKeyword("if")
        buffer.addSpace()
        buffer.add(FormatToken.OpenParen)
        expressionEmitter.emit(node.condition, buffer)
        buffer.add(FormatToken.CloseParen)

        appendOpenBrace(buffer)
        buffer.withIndent { emitStatements(node.ifBranch, buffer) }
        ensureBlockClosingLine(node.ifBranch, buffer)
        buffer.add(FormatToken.CloseBrace)

        if (node.elseBranch.isNotEmpty()) {
            if (config.braceStyle == BraceStyle.SAME_LINE) {
                buffer.addSpace()
                buffer.addKeyword("else")
            } else {
                buffer.newline()
                buffer.addKeyword("else")
            }
            appendOpenBrace(buffer)
            buffer.withIndent { emitStatements(node.elseBranch, buffer) }
            ensureBlockClosingLine(node.elseBranch, buffer)
            buffer.add(FormatToken.CloseBrace)
        }

        if (!isLastInBlock) {
            buffer.newline()
        } else {
            buffer.newline()
        }
    }

    private fun appendOpenBrace(buffer: TokenBuffer) {
        when (config.braceStyle) {
            BraceStyle.SAME_LINE -> {
                buffer.addSpace()
                buffer.add(FormatToken.OpenBrace)
                buffer.newline()
            }

            BraceStyle.NEXT_LINE -> {
                buffer.newline()
                buffer.add(FormatToken.OpenBrace)
                buffer.newline()
            }
        }
    }

    private fun ensureBlockClosingLine(
        statements: List<ASTNode>,
        buffer: TokenBuffer,
    ) {
        if (buffer.isAtLineStart()) {
            return
        }

        if (statements.isEmpty()) {
            buffer.markLineStart()
            return
        }

        if (!config.lineJumpAfterSemicolon || statements.last() is IfElseNode) {
            buffer.newline()
        }
    }
}
