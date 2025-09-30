
package org.printscript.linter.rules

import org.printscript.linter.issue.Issue
import org.printscript.linter.issue.Severity
import org.printscript.linter.util.rangeOf
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.token.TokenType

class ReadInputPromptRule : Rule {
    override fun check(
        node: ASTNode,
        lintContext: LintContext,
    ): List<Issue> {
        if (node !is ReadInputNode) return emptyList()

        val prompt = node.expression
        val isValid =
            when (prompt) {
                is LiteralNode<*> ->
                    prompt.tokenType == TokenType.STRING || prompt.tokenType == TokenType.IDENTIFIER
                else -> false
            }

        if (isValid) return emptyList()

        val range = rangeOf(node)
        return listOf(
            Issue(
                ruleId = "read-input-prompt",
                message = "readInput solo admite literales de cadena o identificadores como prompt",
                startLine = range.sl,
                startCol = range.sc,
                endLine = range.el,
                endCol = range.ec,
                severity = Severity.ERROR,
            ),
        )
    }
}
