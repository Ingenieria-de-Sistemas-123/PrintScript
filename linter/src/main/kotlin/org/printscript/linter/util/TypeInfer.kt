package org.printscript.linter.util

import org.printscript.linter.rules.LintContext
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode

fun inferType(
    expr: ASTNode,
    ctx: LintContext,
): String? =
    when (expr) {
        is LiteralNode<*> ->
            when (expr.type) {
                "number" -> "number"
                "string" -> "string"
                "identifier" -> ctx.symbols[expr.value.toString()]
                else -> null
            }
        is DoubleExpressionNode ->
            when (expr.operator) {
                "+" -> {
                    val lt = inferType(expr.left, ctx)
                    val rt = inferType(expr.right, ctx)
                    when {
                        lt == "string" || rt == "string" -> "string"
                        lt == "number" && rt == "number" -> "number"
                        else -> null
                    }
                }
                "-", "*", "/" -> {
                    val lt = inferType(expr.left, ctx)
                    val rt = inferType(expr.right, ctx)
                    if (lt == "number" && rt == "number") "number" else null
                }
                else -> null
            }
        is AssignationNode -> inferType(expr.type, ctx)
        else -> null
    }
