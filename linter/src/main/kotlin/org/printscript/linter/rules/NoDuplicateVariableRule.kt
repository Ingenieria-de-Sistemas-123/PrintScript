package org.printscript.linter.rules

import org.printscript.linter.issue.Issue
import org.printscript.linter.issue.Severity
import org.printscript.linter.util.idRange
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DeclarationNode

class NoDuplicateVariableRule : Rule {
    override fun check(node: ASTNode, lintContext: LintContext): List<Issue> {
        if (node is DeclarationNode) {
            val name = node.identifier
            val type = node.valueType
            val prev = lintContext.symbols.putIfAbsent(name, type)
            if (prev != null) {
                val r = idRange(name, node.position)
                return listOf(
                    Issue(
                        ruleId = "no-duplicate-var",
                        message = "Variable '$name' ya declarada previamente",
                        startLine = r.sl, startCol = r.sc, endLine = r.el, endCol = r.ec,
                        severity = Severity.ERROR,
                    ),
                )
            }
        }
        return emptyList()
    }
}