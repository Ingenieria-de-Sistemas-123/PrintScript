package org.printscript.rules

import node.ASTNode
import node.LiteralNode
import node.PrintNode
import org.printscript.issue.Issue
import org.printscript.issue.Severity
import org.printscript.range.rangeOf

class PrintlnRestrictionRule : Rule {
    override fun check(node: ASTNode, ctx: LintContext): List<Issue> {
        if (node is PrintNode) {
            val ok = node.expression is LiteralNode<*> &&
                    ((node.expression as LiteralNode<*>).type == "string" || (node.expression as LiteralNode<*>).type == "identifier")
            if (!ok) {
                val r = rangeOf(node)
                return listOf(
                    Issue(
                        ruleId = "println-restriction",
                        message = "println solo admite literal o identificador (no expresiones)",
                        startLine = r.sl, startCol = r.sc, endLine = r.el, endCol = r.ec,
                        severity = Severity.WARNING
                    )
                )
            }
        }
        return emptyList()
    }
}