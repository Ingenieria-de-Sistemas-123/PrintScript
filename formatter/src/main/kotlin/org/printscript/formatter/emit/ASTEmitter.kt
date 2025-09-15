package org.printscript.formatter.emit

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken
import org.printscript.formatter.rules.FormatToken.CloseParen
import org.printscript.formatter.rules.FormatToken.Colon
import org.printscript.formatter.rules.FormatToken.Equals
import org.printscript.formatter.rules.FormatToken.Ident
import org.printscript.formatter.rules.FormatToken.Keyword
import org.printscript.formatter.rules.FormatToken.NewLine
import org.printscript.formatter.rules.FormatToken.NumberLit
import org.printscript.formatter.rules.FormatToken.Op
import org.printscript.formatter.rules.FormatToken.OpKind
import org.printscript.formatter.rules.FormatToken.OpenParen
import org.printscript.formatter.rules.FormatToken.Semicolon
import org.printscript.formatter.rules.FormatToken.StringLit
import org.printscript.formatter.rules.FormatToken.TypeName
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode

class ASTEmitter(private val cfg: FormatterConfig) {
    fun emitProgram(program: List<ASTNode>): List<FormatToken> {
        val out = mutableListOf<FormatToken>()
        program.forEach { emitStmt(it, out) }
        return out
    }

    private fun emitStmt(
        n: ASTNode,
        sink: MutableList<FormatToken>,
    ) {
        when (n) {
            is DeclarationNode -> {
                sink += Keyword("let")
                sink += Ident(n.identifier)
                sink += Colon
                sink += TypeName(n.valueType)
                sink += Equals
                emitExpr(n.expression, sink)
                sink += Semicolon
            }

            is AssignationNode -> {
                sink += Ident(n.variable)
                sink += Equals
                emitExpr(n.expression, sink)
                sink += Semicolon
            }

            is PrintStatementNode -> {
                repeat(cfg.lineJumpBeforePrintln) { sink += NewLine() }
                sink += Keyword("println")
                sink += OpenParen
                emitExpr(n.expression, sink)
                sink += CloseParen
                sink += Semicolon
            }

            else -> error("Sentencia no soportada por el formatter: ${n::class.simpleName}")
        }
    }

    private fun emitExpr(
        n: ASTNode,
        sink: MutableList<FormatToken>,
    ) {
        when (n) {
            is DoubleExpressionNode -> {
                emitExpr(n.left, sink)
                sink +=
                    Op(
                        when (n.operator.trim()) {
                            "+" -> OpKind.PLUS
                            "-" -> OpKind.MINUS
                            "*" -> OpKind.STAR
                            "/" -> OpKind.SLASH
                            else -> error("Operador no soportado: '${n.operator}'")
                        },
                    )
                emitExpr(n.right, sink)
            }

            is LiteralNode<*> -> emitLiteral(n, sink)

            else -> error("Expresión no soportada por el formatter: ${n::class.simpleName}")
        }
    }

    private fun emitLiteral(
        n: LiteralNode<*>,
        sink: MutableList<FormatToken>,
    ) {
        when (val v = n.value) {
            is Number -> sink += NumberLit(v.toString())
            is String -> {
                if (IDENT_REGEX.matches(v)) sink += Ident(v) else sink += StringLit(v)
            }
            is Boolean -> {
                sink += Ident(v.toString())
            }
            else -> error("Literal no soportado: value='$v' (${v?.let { it::class.simpleName }})")
        }
    }

    companion object {
        private val IDENT_REGEX = Regex("^[A-Za-z_][A-Za-z0-9_]*$")
    }
}
