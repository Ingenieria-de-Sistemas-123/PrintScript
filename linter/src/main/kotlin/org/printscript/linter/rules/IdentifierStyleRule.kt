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
        val identifier: String
        val position =
            when (node) {
                is DeclarationNode -> {
                    identifier = node.identifier
                    node.position
                }
                is AssignationNode -> {
                    identifier = node.variable
                    node.position
                }
                else -> return emptyList()
            }

        val style = lintContext.config.identifierStyle
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
        identifier: String,
        style: IdentifierStyle,
    ): Boolean =
        when (style) {
            IdentifierStyle.CAMEL_CASE -> CAMEL_CASE.matches(identifier)
            IdentifierStyle.SNAKE_CASE -> SNAKE_CASE.matches(identifier)
        }

    companion object {
        private val CAMEL_CASE = Regex("^[a-z][A-Za-z0-9]*\$")
        private val SNAKE_CASE = Regex("^[a-z]+(?:_[a-z0-9]+)*\$")
    }
}
