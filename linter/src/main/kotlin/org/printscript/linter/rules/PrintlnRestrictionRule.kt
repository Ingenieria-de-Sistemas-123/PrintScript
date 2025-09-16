package org.printscript.linter.rules

import org.printscript.linter.issue.Issue
import org.printscript.linter.issue.Severity
import org.printscript.linter.util.rangeOf
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode

class PrintlnRestrictionRule : Rule {
    override fun check(
        node: ASTNode,
        lintContext: LintContext,
    ): List<Issue> {
        if (node is PrintStatementNode) {
            val expr = node.expression
            val ok = expr is LiteralNode<*> && (expr.value is String)
            if (!ok) {
                val r = rangeOf(node)
                return listOf(
                    Issue(
                        ruleId = "println-restriction",
                        message = "println solo admite literal o identificador (no expresiones)",
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
