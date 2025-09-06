package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.printscript.common.Position
import org.printscript.linter.issue.Issue
import org.printscript.linter.rules.LintContext
import org.printscript.linter.rules.Rule
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.LiteralNode
import kotlin.test.Test

class LinterTest {
    class AlwaysIssueRule : Rule {
        override fun check(
            node: ASTNode,
            ctx: LintContext,
        ): List<Issue> =
            listOf(
                Issue(
                    ruleId = "always",
                    message = "Siempre issue",
                    startLine = 1,
                    startCol = 1,
                    endLine = 1,
                    endCol = 2,
                ),
            )
    }

    class NeverIssueRule : Rule {
        override fun check(
            node: ASTNode,
            ctx: LintContext,
        ): List<Issue> = emptyList()
    }

    @Test
    fun `sin reglas no devuelve issues`() {
        val node = DeclarationNode("x", "number", LiteralNode(1, "number", Position(1, 1)), Position(1, 1))
        val linter = Linter(emptyList())
        val result = linter.analyze(listOf(node))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `con regla que siempre devuelve issue`() {
        val node = DeclarationNode("x", "number", LiteralNode(1, "number", Position(1, 1)), Position(1, 1))
        val linter = Linter(listOf(AlwaysIssueRule()))
        val result = linter.analyze(listOf(node))
        assertEquals(1, result.size)
        assertEquals("always", result[0].ruleId)
    }

    @Test
    fun `con varias reglas, suma issues`() {
        val node = DeclarationNode("x", "number", LiteralNode(1, "number", Position(1, 1)), Position(1, 1))
        val linter = Linter(listOf(AlwaysIssueRule(), AlwaysIssueRule()))
        val result = linter.analyze(listOf(node))
        assertEquals(2, result.size)
    }

    @Test
    fun `sin nodos no devuelve issues`() {
        val linter = Linter(listOf(AlwaysIssueRule()))
        val result = linter.analyze(emptyList())
        assertTrue(result.isEmpty())
    }
}
