package org.printscript.formatter

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.ApplyContext
import org.printscript.formatter.rules.CodeFormatRule
import org.printscript.formatter.interfaces.FormatToken


/**
 * Aplica el conjunto de reglas sobre una SECUENCIA de tokens de formateo.
 * No conoce el AST. El visitor ya entregó tokens semánticos.
 *
 * Responsabilidades:
 * - Iterar tokens manteniendo prev/next para el contexto.
 * - Delegar en la regla correspondiente o renderizar tokens “triviales”.
 * - Garantizar '\n' final y ';' con salto según config.
 */
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
                // Puntuación/estructura trivial
                is FormatToken.Semicolon -> {
                    out.append(';')
                    if (config.lineJumpAfterSemicolon) out.append('\n')
                }
                is FormatToken.NewLine -> repeat(t.count) { out.append('\n') }
                is FormatToken.Indent  -> out.append(" ".repeat(t.spaces))

                // Literales y atómicos “de contenido” (NO se les aplican reglas de spacing)
                is FormatToken.StringLit -> out.append('"').append(t.raw).append('"')
                is FormatToken.Keyword   -> out.append(t.text)
                is FormatToken.Ident     -> out.append(t.text)
                is FormatToken.TypeName  -> out.append(t.text)
                is FormatToken.NumberLit -> out.append(t.raw)
                is FormatToken.OpenParen -> out.append('(')
                is FormatToken.CloseParen-> out.append(')')
                is FormatToken.Comma     -> out.append(", ")

                // Tokens que sí pasan por reglas (espacios alrededor, etc.)
                is FormatToken.Equals, is FormatToken.Colon, is FormatToken.Op -> {
                    val rule = rules.firstOrNull { it.matches(t) }
                    if (rule != null) rule.apply(t, ctx) else out.append(renderRaw(t))
                }
            }
        }
        if (out.isNotEmpty() && out.last() != '\n') out.append('\n')
        return out.toString()
    }

    // Fallback de seguridad (en teoría no se usa para Op/Equals/Colon)
    private fun renderRaw(t: FormatToken): String = when (t) {
        is FormatToken.Equals -> "="
        is FormatToken.Colon  -> ":"
        is FormatToken.Op     -> error("Op debe ser formateado por regla")
        else -> ""
    }
}
