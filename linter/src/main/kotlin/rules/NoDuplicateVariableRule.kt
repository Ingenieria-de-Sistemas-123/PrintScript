package org.printscript.rules

import node.ASTNode
import node.DeclarationNode
import org.printscript.issue.Issue
import org.printscript.issue.Severity
import org.printscript.range.*

class NoDuplicateVariableRule : Rule {
    override fun check(node: ASTNode, ctx: LintContext): List<Issue> {
        if (node is DeclarationNode) {
            val prev = ctx.symbols.putIfAbsent(node.name, node.type)
            if (prev != null) {
                val r = idRange(node.name, node.position)
                return listOf(
                    Issue(
                        ruleId = "no-duplicate-var",
                        message = "Variable '${node.name}' ya declarada previamente",
                        startLine = r.sl, startCol = r.sc, endLine = r.el, endCol = r.ec,
                        severity = Severity.ERROR
                    )
                )
            }
        }
        return emptyList()
    }
}