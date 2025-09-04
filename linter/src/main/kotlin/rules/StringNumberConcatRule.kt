package org.printscript.rules

import node.ASTNode
import node.DoubleExpressionNode
import org.printscript.issue.Issue
import org.printscript.issue.Severity
import org.printscript.range.inferType
import org.printscript.range.rangeOf

class StringNumberConcatRule : Rule {
    override fun check(node: ASTNode, ctx: LintContext): List<Issue> {
        if (node is DoubleExpressionNode && node.operator == "+") {
            val lt = inferType(node.left, ctx)
            val rt = inferType(node.right, ctx)
            val mixed = (lt == "string" && rt == "number") || (lt == "number" && rt == "string")
            if (mixed) {
                val r = rangeOf(node)
                return listOf(
                    Issue(
                        ruleId = "string-number-concat",
                        message = "Concatenación mixta string+number: considerá cast explícito",
                        startLine = r.sl, startCol = r.sc, endLine = r.el, endCol = r.ec,
                        severity = Severity.WARNING
                    )
                )
            }
        }
        return emptyList()
    }
}