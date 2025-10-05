package org.printscript.formatter.render

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.formatter.config.BraceStyle
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken.CloseBrace
import org.printscript.formatter.rules.FormatToken.CloseParen
import org.printscript.formatter.rules.FormatToken.Colon
import org.printscript.formatter.rules.FormatToken.Ident
import org.printscript.formatter.rules.FormatToken.Indent
import org.printscript.formatter.rules.FormatToken.Keyword
import org.printscript.formatter.rules.FormatToken.NewLine
import org.printscript.formatter.rules.FormatToken.OpenBrace
import org.printscript.formatter.rules.FormatToken.OpenParen
import org.printscript.formatter.rules.FormatToken.Semicolon
import org.printscript.formatter.rules.FormatToken.Space
import org.printscript.formatter.rules.FormatToken.StringLit
import org.printscript.formatter.rules.FormatToken.TypeName

class RuleApplierAdditionalTest {
    @Test
    fun `trimming de newlines finales`() {
        val toks =
            listOf(
                Keyword("println"),
                OpenParen,
                StringLit("x"),
                CloseParen,
                Semicolon,
                NewLine(2),
            )
        val out = RuleApplier(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).apply(toks)
        assertEquals("println(\"x\");", out)
    }

    @Test
    fun `space token inicial es ignorado`() {
        val toks = listOf(Space, Keyword("let"), Space, Ident("a"), Colon, TypeName("number"), Semicolon)
        val out = RuleApplier(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).apply(toks)
        // Formato esperado con reglas por defecto: no espacio antes de ':', espacio después, salto luego de ';'
        assertEquals("let a: number;", out)
    }

    @Test
    fun `indent token se preserva tras newline`() {
        val toks =
            listOf(
                Keyword("if"), Space, OpenParen, Ident("cond"), CloseParen, Space, OpenBrace, NewLine(),
                Indent(4), Keyword("println"), OpenParen, StringLit("ok"), CloseParen, Semicolon, CloseBrace,
            )
        val out = RuleApplier(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).apply(toks)
        assertEquals("if (cond) {\n    println(\"ok\");\n}", out)
    }

    @Test
    fun `single space separation respects literals`() {
        val toks = listOf(Keyword("println"), OpenParen, StringLit("a b"), CloseParen, Semicolon)
        val cfg = FormatterConfig(singleSpaceSeparation = true, braceStyle = BraceStyle.SAME_LINE)

        val out = RuleApplier(cfg).apply(toks)

        assertEquals("println ( \"a b\" ) ;", out)
    }
}
