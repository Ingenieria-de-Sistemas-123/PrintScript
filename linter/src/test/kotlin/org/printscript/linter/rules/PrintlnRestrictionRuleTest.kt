package org.printscript.linter.rules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.issue.Severity
import org.printscript.linter.testutil.AstFactory

class PrintlnRestrictionRuleTest {
    @Test
    fun acepta_literal_string_y_identificador() {
        val ctx = LintContext(org.printscript.linter.LintConfig())
        val rule = PrintlnRestrictionRule()

        val okString = AstFactory.print(AstFactory.litString("\"hola\""), 4, 2)
        val okId = AstFactory.print(AstFactory.litIdentifier("x"), 5, 3)

        assertTrue(rule.check(okString, ctx).isEmpty())
        assertTrue(rule.check(okId, ctx).isEmpty())
    }

    @Test
    fun rechaza_literal_number_y_expresiones_compuestas() {
        val ctx = LintContext(org.printscript.linter.LintConfig())
        val rule = PrintlnRestrictionRule()

        val badNumber = AstFactory.print(AstFactory.litNumber("7"), 6, 1)
        val badExpr =
            AstFactory.print(
                AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litNumber("2")),
                7,
                1,
            )

        val i1 = rule.check(badNumber, ctx)
        val i2 = rule.check(badExpr, ctx)

        assertEquals(1, i1.size)
        assertEquals("println-restriction", i1.first().ruleId)
        assertEquals(Severity.WARNING, i1.first().severity)
        assertEquals(6, i1.first().startLine)
        assertEquals(1, i1.first().startCol)

        assertEquals(1, i2.size)
        assertEquals(7, i2.first().startLine)
        assertEquals(1, i2.first().startCol)
    }
}
