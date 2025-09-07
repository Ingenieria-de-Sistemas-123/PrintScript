package org.printscript.linter.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.printscript.linter.LintConfig
import org.printscript.linter.rules.LintContext
import org.printscript.linter.testutil.AstFactory

class InferTypeTest {
    private fun ctxWith(vararg pairs: Pair<String, String>): LintContext = LintContext(LintConfig()).also { it.symbols.putAll(pairs) }

    @Test
    fun literales_y_identificadores() {
        val ctx = ctxWith("x" to "number", "s" to "string")
        assertEquals("number", inferType(AstFactory.litNumber("42"), ctx))
        assertEquals("string", inferType(AstFactory.litString("\"hi\""), ctx))
        assertEquals("number", inferType(AstFactory.litIdentifier("x"), ctx))
        assertEquals("string", inferType(AstFactory.litIdentifier("s"), ctx))
        assertNull(inferType(AstFactory.litIdentifier("unknown"), ctx))
    }

    @Test
    fun suma_resultado_string_si_alguno_es_string() {
        val ctx = ctxWith()
        val sn = AstFactory.bin(AstFactory.litString("\"a\""), "+", AstFactory.litNumber("1"))
        val ns = AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litString("\"a\""))
        val ss = AstFactory.bin(AstFactory.litString("\"a\""), "+", AstFactory.litString("\"b\""))
        val nn = AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litNumber("2"))
        assertEquals("string", inferType(sn, ctx))
        assertEquals("string", inferType(ns, ctx))
        assertEquals("string", inferType(ss, ctx))
        assertEquals("number", inferType(nn, ctx))
    }

    @Test
    fun ops_aritmeticos_menos_por_div() {
        val ctx = ctxWith()
        val okMinus = AstFactory.bin(AstFactory.litNumber("2"), "-", AstFactory.litNumber("1"))
        val badMinus = AstFactory.bin(AstFactory.litString("\"a\""), "-", AstFactory.litNumber("1"))
        val okMul = AstFactory.bin(AstFactory.litNumber("2"), "*", AstFactory.litNumber("3"))
        val badDiv = AstFactory.bin(AstFactory.litNumber("2"), "/", AstFactory.litString("\"x\""))
        assertEquals("number", inferType(okMinus, ctx))
        assertNull(inferType(badMinus, ctx))
        assertEquals("number", inferType(okMul, ctx))
        assertNull(inferType(badDiv, ctx))
    }

    @Test
    fun operador_desconocido_da_null_y_assignation_reenvia() {
        val ctx = ctxWith()
        val unknown = AstFactory.bin(AstFactory.litNumber("1"), "%", AstFactory.litNumber("2"))
        assertNull(inferType(unknown, ctx))

        val assign =
            AstFactory.assign(
                "a",
                AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litNumber("2")),
            )
        assertEquals("number", inferType(assign, ctx))
    }
}
