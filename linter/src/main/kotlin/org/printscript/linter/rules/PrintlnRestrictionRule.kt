package org.printscript.linter.rules

import org.printscript.linter.issue.Issue
import org.printscript.linter.issue.Severity
import org.printscript.linter.util.rangeOf
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintNode

class PrintlnRestrictionRule : Rule {
    override fun check(
        node: ASTNode,
        ctx: LintContext,
    ): List<Issue> {
        if (node is PrintNode) {
            val ok =
                node.expression is LiteralNode<*> &&
                    ((node.expression as LiteralNode<*>).type == "string" || (node.expression as LiteralNode<*>).type == "identifier")
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
