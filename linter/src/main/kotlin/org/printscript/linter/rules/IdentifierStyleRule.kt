package org.printscript.linter.rules

import org.printscript.linter.IdentifierStyle
import org.printscript.linter.issue.Issue
import org.printscript.linter.issue.Severity
import org.printscript.linter.util.idRange
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode

class IdentifierStyleRule : Rule {
    override fun check(
        node: ASTNode,
        lintContext: LintContext,
    ): List<Issue> {
        val style = lintContext.config.identifierStyle ?: return emptyList() // sin estilo -> no aplica

        val (identifier, position) =
            when (node) {
                is DeclarationNode -> node.identifier to node.position
                is AssignationNode -> node.variable to node.position
                else -> return emptyList()
            }

        if (isValid(identifier, style)) return emptyList()

        val range = idRange(identifier, position)
        val expected =
            when (style) {
                IdentifierStyle.CAMEL_CASE -> "camelCase"
                IdentifierStyle.SNAKE_CASE -> "snake_case"
            }

        return listOf(
            Issue(
                ruleId = "identifier-style",
                message = "Identificador '$identifier' debe respetar el estilo $expected",
                startLine = range.sl,
                startCol = range.sc,
                endLine = range.el,
                endCol = range.ec,
                severity = Severity.ERROR,
            ),
        )
    }

    private fun isValid(
        name: String,
        style: IdentifierStyle,
    ): Boolean =
        when (style) {
            IdentifierStyle.CAMEL_CASE -> CAMEL_REGEX.matches(name)
            IdentifierStyle.SNAKE_CASE -> SNAKE_REGEX.matches(name)
        }

    companion object {
        private val CAMEL_REGEX = Regex("^[a-z]+(?:[A-Z][a-z0-9]*)*$")
        private val SNAKE_REGEX = Regex("^[a-z]+(?:_[a-z0-9]+)*$")
    }
}
