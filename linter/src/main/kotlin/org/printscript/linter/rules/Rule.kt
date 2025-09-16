package org.printscript.linter.rules

import org.printscript.linter.issue.Issue
import org.printscript.parser.node.ASTNode

interface Rule {
    fun check(
        node: ASTNode,
        lintContext: LintContext,
    ): List<Issue>
}
