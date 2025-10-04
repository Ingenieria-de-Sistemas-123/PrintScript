package org.printscript.formatter.render

import org.printscript.formatter.rules.ApplyContext
import org.printscript.formatter.rules.FormatToken

/**
 * Contrato para renderizar un token cuando ninguna regla específica aplica.
 */
fun interface FormatTokenRenderer {
    fun render(
        token: FormatToken,
        ctx: ApplyContext,
    )
}
