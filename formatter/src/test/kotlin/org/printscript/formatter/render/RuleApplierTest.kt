package org.printscript.formatter.render

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken
import org.printscript.formatter.rules.FormatToken.Colon
import org.printscript.formatter.rules.FormatToken.Equals
import org.printscript.formatter.rules.FormatToken.Keyword
import org.printscript.formatter.rules.FormatToken.NumberLit
import org.printscript.formatter.rules.FormatToken.Ident
import org.printscript.formatter.rules.FormatToken.Op
import org.printscript.formatter.rules.FormatToken.OpKind
import org.printscript.formatter.rules.FormatToken.Semicolon
import org.printscript.formatter.rules.FormatToken.TypeName

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RuleApplierTest {

    @Test
    fun `espacios alrededor de '=' on`() {
        val toks = listOf(FormatToken.Ident("x"), Equals, NumberLit("5.0"), Semicolon)
        val out = RuleApplier(FormatterConfig(spaceAroundEquals = true)).apply(toks)
        assertEquals("x = 5.0;\n", out)
    }

    @Test
    fun `espacios alrededor de '=' off`() {
        val toks = listOf(Ident("x"), Equals, NumberLit("5.0"), Semicolon)
        val out = RuleApplier(FormatterConfig(spaceAroundEquals = false)).apply(toks)
        assertEquals("x=5.0;\n", out)
    }

    @Test
    fun `variantes de colon`() {
        val lhs = listOf(Keyword("let"), Ident("a"))
        val rhs = listOf(TypeName("number"), Semicolon)

        val both = RuleApplier(FormatterConfig(spaceBeforeColon = true, spaceAfterColon = true))
            .apply(lhs + Colon + rhs)
        assertEquals("let a : number;\n", both)

        val onlyAfter = RuleApplier(FormatterConfig(spaceBeforeColon = false, spaceAfterColon = true))
            .apply(lhs + Colon + rhs)
        assertEquals("let a: number;\n", onlyAfter)

        val onlyBefore = RuleApplier(FormatterConfig(spaceBeforeColon = true, spaceAfterColon = false))
            .apply(lhs + Colon + rhs)
        assertEquals("let a :number;\n", onlyBefore)

        val none = RuleApplier(FormatterConfig(spaceBeforeColon = false, spaceAfterColon = false))
            .apply(lhs + Colon + rhs)
        assertEquals("let a:number;\n", none)
    }

    @Test
    fun `operadores con espacios y deteccion de '-' unario`() {
        // Caso binario: "1 + 2"
        val bin = RuleApplier(FormatterConfig(spaceAroundOperators = true)).apply(
            listOf(NumberLit("1.0"), Op(OpKind.PLUS), NumberLit("2.0"), Semicolon)
        )
        assertEquals("1.0 + 2.0;\n", bin)

        // Caso unario tras '=': "x = -1"
        val unary = RuleApplier(FormatterConfig(spaceAroundOperators = true)).apply(
            listOf(Ident("x"), Equals, Op(OpKind.MINUS), NumberLit("1.0"), Semicolon)
        )
        assertEquals("x = -1.0;\n", unary)

        // Sin espacios alrededor de operadores
        val noSpaces = RuleApplier(FormatterConfig(spaceAroundOperators = false)).apply(
            listOf(NumberLit("3.0"), Op(OpKind.STAR), NumberLit("4.0"), Semicolon)
        )
        assertEquals("3.0*4.0;\n", noSpaces)
    }

    @Test
    fun `semicolons sin salto intermedio pero salto final`() {
        val toks = listOf(Ident("a"), Equals, NumberLit("1.0"), Semicolon,
            Ident("b"), Equals, NumberLit("2.0"), Semicolon)
        val out = RuleApplier(FormatterConfig(lineJumpAfterSemicolon = false, spaceAroundEquals = true))
            .apply(toks)
        assertEquals("a = 1.0;b = 2.0;\n", out)
    }
}
