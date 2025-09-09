package org.printscript.cli.adapters

import org.printscript.linter.LintConfig
import org.printscript.linter.Linter
import org.printscript.linter.issue.Issue
import org.printscript.linter.rules.NoDuplicateVariableRule
import org.printscript.linter.rules.PrintlnRestrictionRule
import org.printscript.linter.rules.StringNumberConcatRule
import org.printscript.parser.node.ASTNode

class LinterAdapter(
    private val config: LintConfig = LintConfig(),
    private val linter: Linter =
        Linter(
            rules =
                listOf(
                    NoDuplicateVariableRule(),
                    PrintlnRestrictionRule(),
                    StringNumberConcatRule(),
                ),
            config = config,
        ),
) {
    fun analyze(ast: List<ASTNode>): List<Issue> = linter.analyze(ast)
}
