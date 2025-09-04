package org.printscript.formatter.rules

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.interfaces.FormatToken

/**
 * Regla de formateo:
 * - matches(t) indica si la regla se aplica a ese token (ej: es Colon, Equals, Op, ...).
 * - apply(t, ctx) escribe el texto al StringBuilder con el spacing que corresponda.
 *
 * IMPORTANTE: Las reglas NO ven el AST, sólo reciben el TOKEN y el CONTEXTO prev/next.
 * El AST ya fue recorrido por el visitor y convertido a tokens semánticos.
 */
data class ApplyContext(
    val prev: FormatToken?,
    val next: FormatToken?,
    val cfg: FormatterConfig,
    val out: StringBuilder
)

interface CodeFormatRule {
    fun matches(t: FormatToken): Boolean
    fun apply(t: FormatToken, ctx: ApplyContext)
}
