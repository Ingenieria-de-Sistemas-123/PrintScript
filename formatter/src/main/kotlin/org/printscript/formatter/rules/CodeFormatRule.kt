package org.printscript.formatter.rules

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.layout.OriginalLayoutTracker

data class ApplyContext(
    val prev: FormatToken?,
    val next: FormatToken?,
    val cfg: FormatterConfig,
    val out: StringBuilder,
    val layout: OriginalLayoutTracker?,
)

/** Regla de formateo: decide cómo renderizar un token con su contexto. */
interface CodeFormatRule {
    fun matches(t: FormatToken): Boolean

    fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    )
}
