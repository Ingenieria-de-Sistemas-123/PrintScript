package org.printscript.linter.util

import org.printscript.linter.rules.LintContext
import org.printscript.parser.node.*

fun inferType(expr: ASTNode, lintContext: LintContext): String? =
    when (expr) {
        is LiteralNode<*> ->
            when (val v = expr.value) {
                is Number -> "number"
                is String -> lintContext.symbols[v] ?: "string"
                is Boolean -> "boolean"
                else -> null
            }

        is DoubleExpressionNode ->
            when (expr.operator) {
                "+" -> {
                    val lt = inferType(expr.left, lintContext)
                    val rt = inferType(expr.right, lintContext)
                    when {
                        lt == "string" || rt == "string" -> "string"
                        lt == "number" && rt == "number" -> "number"
                        else -> null
                    }
                }
                "-", "*", "/" -> {
                    val lt = inferType(expr.left, lintContext)
                    val rt = inferType(expr.right, lintContext)
                    if (lt == "number" && rt == "number") "number" else null
                }
                else -> null
            }

        is AssignationNode -> inferType(expr.expression, lintContext)

        else -> null
    }