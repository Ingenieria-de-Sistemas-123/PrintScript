package org.printscript.formatter.rules

/**
 * Controla la inserción del salto de línea tras ';' en función de la
 * configuración activa.
 */
class SemicolonLineBreakRule : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Semicolon

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        ctx.out.append(';')
        if (ctx.cfg.lineJumpAfterSemicolon && ctx.next !is FormatToken.NewLine) {
            ctx.out.append('\n')
        }
    }
}
