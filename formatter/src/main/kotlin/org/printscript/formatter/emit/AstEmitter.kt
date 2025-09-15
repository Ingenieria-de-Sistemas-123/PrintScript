package org.printscript.formatter.emit

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken
import org.printscript.formatter.rules.FormatToken.CloseParen
import org.printscript.formatter.rules.FormatToken.Ident
import org.printscript.formatter.rules.FormatToken.Keyword
import org.printscript.formatter.rules.FormatToken.NewLine
import org.printscript.formatter.rules.FormatToken.NumberLit
import org.printscript.formatter.rules.FormatToken.Op
import org.printscript.formatter.rules.FormatToken.OpKind
import org.printscript.formatter.rules.FormatToken.OpenParen
import org.printscript.formatter.rules.FormatToken.Semicolon
import org.printscript.formatter.rules.FormatToken.StringLit
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode

class AstEmitter(private val cfg: FormatterConfig) {
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
                // let <name> : <type> = <expr> ;
                sink += FormatToken.Keyword("let")
                sink += FormatToken.Ident(n.name)
                sink += FormatToken.Colon
                sink += FormatToken.TypeName(n.type)
                sink += FormatToken.Equals
                emitExpr(n.value, sink)
                sink += FormatToken.Semicolon
            }

            is AssignationNode -> {
                // <name> = <expr> ;
                sink += FormatToken.Ident(n.name)
                sink += FormatToken.Equals
                // En AssignationNode el campo 'type' es el VALOR a asignar
                emitExpr(n.type, sink)
                sink += FormatToken.Semicolon
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

            is LiteralNode<*> -> {
                when (n.type.lowercase()) {
                    "number" -> sink += NumberLit(n.value.toString())
                    "string" -> sink += StringLit(n.value.toString())
                    "identifier" -> sink += Ident(n.value.toString())
                    else -> error("Literal no soportado: type='${n.type}' value='${n.value}'")
                }
            }

            else -> error("Expresión no soportada por el formatter: ${n::class.simpleName}")
        }
    }
}
