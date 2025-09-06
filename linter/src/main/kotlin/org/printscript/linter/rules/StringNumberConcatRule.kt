package org.printscript.linter.rules

import org.printscript.linter.issue.Issue
import org.printscript.linter.issue.Severity
import org.printscript.linter.util.inferType
import org.printscript.linter.util.rangeOf
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DoubleExpressionNode

class StringNumberConcatRule : Rule {
    override fun check(
        node: ASTNode,
        ctx: LintContext,
    ): List<Issue> {
        if (node is DoubleExpressionNode && node.operator == "+") {
            val lt = inferType(node.left, ctx) ?: return emptyList() // Tipo izquierdo
            val rt = inferType(node.right, ctx) ?: return emptyList() // Tipo derecho

            val mixed = (lt == "string" && rt == "number") || (lt == "number" && rt == "string")
            if (mixed) {
                val r = rangeOf(node)
                return listOf(
                    Issue(
                        ruleId = "string-number-concat",
                        message = "Concatenación mixta string+number: considerá cast explícito",
                        startLine = r.sl,
                        startCol = r.sc,
                        endLine = r.el,
                        endCol = r.ec,
                        severity = Severity.WARNING,
                    ),
                )
            }
        }
        return emptyList()
    }
}
