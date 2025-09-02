package org.printscript

import node.ASTNode
import org.printscript.issue.Issue
import org.printscript.rules.LintContext
import org.printscript.rules.Rule

class Linter(
    private val rules: List<Rule>,
    private val config: LintConfig = LintConfig()
) {
    fun analyze(program: List<ASTNode>): List<Issue> {
        val ctx = LintContext(config)
        val out = mutableListOf<Issue>()
        for (node in program) {
            for (rule in rules) {
                out.addAll(rule.check(node, ctx))
            }
        }
        return out
    }
}
