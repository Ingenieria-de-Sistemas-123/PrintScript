package org.printscript.linter.rules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.LintConfig
import org.printscript.linter.issue.Severity
import org.printscript.linter.testutil.AstFactory

class NoDuplicateVariableRuleTest {
    @Test
    fun primera_declaracion_no_reporta_y_segunda_iguales_si() {
        val ctx = LintContext(LintConfig())
        val rule = NoDuplicateVariableRule()

        val d1 = AstFactory.decl("x", "number", AstFactory.litNumber("0"), 2, 5)
        val issues1 = rule.check(d1, ctx)
        assertTrue(issues1.isEmpty())
        // x ya quedó en ctx.symbols

        val d2 = AstFactory.decl("x", "number", AstFactory.litNumber("1"), 3, 4)
        val issues2 = rule.check(d2, ctx)
        assertEquals(1, issues2.size)
        val i = issues2.first()
        assertEquals("no-duplicate-var", i.ruleId)
        assertEquals(Severity.ERROR, i.severity)
        assertTrue(i.message.contains("ya declarada previamente"))
        // idRange: desde 3:4 hasta 3:(4 + len - 1 = 4)
        assertEquals(3, i.startLine)
        assertEquals(4, i.startCol)
        assertEquals(3, i.endLine)
        assertEquals(4, i.endCol)
    }

    @Test
    fun declaracion_con_nombre_distinto_no_reporta() {
        val ctx = LintContext(LintConfig())
        val rule = NoDuplicateVariableRule()
        rule.check(AstFactory.decl("a", "number", AstFactory.litNumber("0"), 1, 1), ctx)
        val issues = rule.check(AstFactory.decl("b", "number", AstFactory.litNumber("1"), 1, 5), ctx)
        assertTrue(issues.isEmpty())
    }
}
