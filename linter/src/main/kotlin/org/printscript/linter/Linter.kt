package org.printscript.linter

import org.printscript.linter.issue.Issue
import org.printscript.linter.rules.LintContext
import org.printscript.linter.rules.Rule
import org.printscript.parser.node.ASTNode

class Linter(
    private val rules: List<Rule>,
    private val config: LintConfig = LintConfig(),
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
