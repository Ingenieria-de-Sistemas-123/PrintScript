package org.printscript.formatter.render

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.ApplyContext
import org.printscript.formatter.rules.SpaceAroundColon
import org.printscript.formatter.rules.SpaceAroundEquals
import org.printscript.formatter.rules.SpaceAroundOperator
import org.printscript.formatter.rules.CodeFormatRule
import org.printscript.formatter.rules.FormatToken

class RuleApplier(
    private val config: FormatterConfig,
    private val rules: List<CodeFormatRule> = listOf(
        SpaceAroundEquals(),
        SpaceAroundColon(),
        SpaceAroundOperator()
    )
) {
    fun apply(tokens: List<FormatToken>): String {
        val out = StringBuilder()

        for (i in tokens.indices) {
            val t = tokens[i]
            val ctx = ApplyContext(tokens.getOrNull(i - 1), tokens.getOrNull(i + 1), config, out)

            when (t) {
                // layout
                is FormatToken.NewLine -> repeat(t.count) { out.append('\n') }
                is FormatToken.Indent  -> out.append(" ".repeat(t.spaces))

                // estructura
                is FormatToken.OpenParen  -> out.append('(')
                is FormatToken.CloseParen -> out.append(')')
                is FormatToken.Comma      -> out.append(", ")
                is FormatToken.Semicolon  -> {
                    out.append(';')
                    if (config.lineJumpAfterSemicolon) out.append('\n')
                }

                // contenido literal
                is FormatToken.Keyword   -> out.append(t.text)
                is FormatToken.Ident     -> out.append(t.text)
                is FormatToken.TypeName  -> out.append(t.text)
                is FormatToken.NumberLit -> out.append(t.raw)
                is FormatToken.StringLit -> out.append('"').append(t.raw).append('"')

                // tokens con reglas
                is FormatToken.Equals, is FormatToken.Colon, is FormatToken.Op -> {
                    val rule = rules.firstOrNull { it.matches(t) }
                    if (rule != null) rule.apply(t, ctx) else out.append(renderRaw(t))
                }
            }
        }

        if (out.isNotEmpty() && out.last() != '\n') out.append('\n')
        return out.toString()
    }

    private fun renderRaw(t: FormatToken): String = when (t) {
        is FormatToken.Equals -> "="
        is FormatToken.Colon  -> ":"
        is FormatToken.Op     -> error("Operator debe ser formateado por regla")
        else -> ""
    }
}
