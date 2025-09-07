package org.printscript.linter.rules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.linter.issue.Severity
import org.printscript.linter.testutil.AstFactory

class StringNumberConcatRuleTest {
    @Test
    fun reporta_en_concatenacion_mixta_y_no_en_homogeneas() {
        val ctx = LintContext(org.printscript.linter.LintConfig())
        val rule = StringNumberConcatRule()

        val sn = AstFactory.bin(AstFactory.litString("\"a\""), "+", AstFactory.litNumber("1"), 2, 2)
        val ns = AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litString("\"b\""), 3, 3)
        val nn = AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litNumber("2"), 4, 4)
        val ss = AstFactory.bin(AstFactory.litString("\"a\""), "+", AstFactory.litString("\"b\""), 5, 5)

        val i1 = rule.check(sn, ctx)
        assertEquals(1, i1.size)
        assertEquals(Severity.WARNING, i1.first().severity)
        val i2 = rule.check(ns, ctx)
        assertEquals(1, i2.size)
        assertTrue(rule.check(nn, ctx).isEmpty())
        assertTrue(rule.check(ss, ctx).isEmpty())
    }

    @Test
    fun no_reporta_cuando_inferType_no_puede_determinar() {
        val ctx = LintContext(org.printscript.linter.LintConfig())
        // "id" no está en symbols => inferType(identifier) == null -> la regla retorna vacío
        val unknown = AstFactory.bin(AstFactory.litIdentifier("id"), "+", AstFactory.litNumber("1"), 6, 1)
        assertTrue(StringNumberConcatRule().check(unknown, ctx).isEmpty())
    }
}
