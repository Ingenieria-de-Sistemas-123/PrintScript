package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.issue.Severity
import org.printscript.linter.rules.IdentifierStyleRule
import org.printscript.linter.rules.LintContext
import org.printscript.linter.rules.NoDuplicateVariableRule
import org.printscript.linter.rules.PrintlnRestrictionRule
import org.printscript.linter.rules.ReadInputPromptRule
import org.printscript.linter.rules.StringNumberConcatRule
import org.printscript.linter.testutil.TestUtils.declConst
import org.printscript.linter.testutil.TestUtils.declVar
import org.printscript.linter.testutil.TestUtils.identifier
import org.printscript.linter.testutil.TestUtils.num
import org.printscript.linter.testutil.TestUtils.plus
import org.printscript.linter.testutil.TestUtils.printlnNode
import org.printscript.linter.testutil.TestUtils.readInput
import org.printscript.linter.testutil.TestUtils.str

class RulesTest {
    @Test
    fun `NoDuplicateVariableRule detects redeclaration`() {
        val rule = NoDuplicateVariableRule()
        val ctx = LintContext(LintConfig())

        val d1 = declVar("x", "number", num(1), 1, 1)
        val d2 = declConst("x", "number", num(2), 2, 5)

        assertTrue(rule.check(d1, ctx).isEmpty())

        val issues = rule.check(d2, ctx)
        assertEquals(1, issues.size)
        val i = issues.first()
        assertEquals("no-duplicate-var", i.ruleId)
        assertEquals(Severity.ERROR, i.severity)
        assertEquals(2, i.startLine)
        assertEquals(5, i.startCol)
        assertEquals(2, i.endLine)
        assertEquals(5, i.endCol)
    }

    @Test
    fun `PrintlnRestrictionRule warns on non-literal`() {
        val rule = PrintlnRestrictionRule()
        val ctx = LintContext(LintConfig())

        assertTrue(rule.check(printlnNode(str("ok")), ctx).isEmpty())

        val issues = rule.check(printlnNode(plus(num(1), num(2))), ctx)
        assertEquals(1, issues.size)
        assertEquals("println-restriction", issues.first().ruleId)
    }

    @Test
    fun `StringNumberConcatRule warns on mixed concat only`() {
        val rule = StringNumberConcatRule()
        val ctx = LintContext(LintConfig())

        val mixed1 = plus(str("a"), num(1))
        val mixed2 = plus(num(1), str("a"))
        val ok1 = plus(num(1), num(2))
        val ok2 = plus(str("a"), str("b"))

        val i1 = rule.check(mixed1, ctx)
        val i2 = rule.check(mixed2, ctx)
        val i3 = rule.check(ok1, ctx)
        val i4 = rule.check(ok2, ctx)

        assertEquals(1, i1.size)
        assertEquals("string-number-concat", i1.first().ruleId)
        assertEquals(1, i2.size)
        assertEquals("string-number-concat", i2.first().ruleId)
        assertTrue(i3.isEmpty())
        assertTrue(i4.isEmpty())
    }

    @Test
    fun `IdentifierStyleRule honours config`() {
        val camelRule = IdentifierStyleRule()
        val camelCtx = LintContext(LintConfig(identifierStyle = IdentifierStyle.CAMEL_CASE))
        val snakeRule = IdentifierStyleRule()
        val snakeCtx = LintContext(LintConfig(identifierStyle = IdentifierStyle.SNAKE_CASE))

        val camelOk = declVar("validName", "string", str("ok"), 1, 1)
        val camelBad = declVar("invalid_name", "string", str("ok"), 2, 1)
        val snakeOk = declVar("valid_name", "string", str("ok"), 3, 1)
        val snakeBad = declVar("invalidName", "string", str("ok"), 4, 1)

        assertTrue(camelRule.check(camelOk, camelCtx).isEmpty())
        assertEquals(1, camelRule.check(camelBad, camelCtx).size)

        assertTrue(snakeRule.check(snakeOk, snakeCtx).isEmpty())
        assertEquals(1, snakeRule.check(snakeBad, snakeCtx).size)
    }

    @Test
    fun `ReadInputPromptRule forbids complex expressions`() {
        val rule = ReadInputPromptRule()
        val ctx = LintContext(LintConfig())

        val literalPrompt = readInput(str("nombre"))
        val identifierPrompt = readInput(identifier("prompt"))
        val invalidPrompt = readInput(plus(str("a"), str("b")))

        assertTrue(rule.check(literalPrompt, ctx).isEmpty())
        assertTrue(rule.check(identifierPrompt, ctx).isEmpty())

        val issues = rule.check(invalidPrompt, ctx)
        assertEquals(1, issues.size)
        assertEquals("read-input-prompt", issues.first().ruleId)
        assertEquals(Severity.ERROR, issues.first().severity)
    }
}
