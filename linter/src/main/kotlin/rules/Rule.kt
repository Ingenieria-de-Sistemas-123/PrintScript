package org.printscript.rules

import node.ASTNode
import org.printscript.LintConfig
import org.printscript.issue.Issue

interface Rule {
    fun check(node: ASTNode, ctx: LintContext): List<Issue>
}
