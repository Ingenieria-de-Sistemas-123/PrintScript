package org.printscript.formatter.rules

import org.printscript.formatter.config.FormatterConfig

data class ApplyContext(
    val prev: FormatToken?,
    val next: FormatToken?,
    val cfg: FormatterConfig,
    val out: StringBuilder,
)

/** Regla de formateo: decide cómo renderizar un token con su contexto. */
interface CodeFormatRule {
    fun matches(t: FormatToken): Boolean

    fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    )
}
