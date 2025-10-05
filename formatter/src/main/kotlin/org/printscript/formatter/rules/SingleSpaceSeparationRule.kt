package org.printscript.formatter.rules

import org.printscript.formatter.render.FormatTokenRenderer

class SingleSpaceSeparationRule(
    private val delegate: FormatTokenRenderer,
) : CodeFormatRule {
    override fun matches(t: FormatToken): Boolean =
        when (t) {
            is FormatToken.Keyword -> true
            is FormatToken.Ident -> true
            is FormatToken.TypeName -> true
            is FormatToken.NumberLit -> true
            is FormatToken.StringLit -> true
            is FormatToken.OpenParen -> true
            is FormatToken.CloseParen -> true
            is FormatToken.OpenBrace -> true
            is FormatToken.CloseBrace -> true
            else -> false
        }

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        enforceSingleSpaceBefore(t, ctx)
        delegate.render(t, ctx)
    }
}

internal fun enforceSingleSpaceBefore(
    token: FormatToken,
    ctx: ApplyContext,
) {
    if (!ctx.cfg.singleSpaceSeparation) return

    val prev = ctx.prev ?: return
    if (token is FormatToken.NewLine || token is FormatToken.Indent || token is FormatToken.Space) return
    if (prev is FormatToken.NewLine || prev is FormatToken.Indent) return
    if (token is FormatToken.Colon || prev is FormatToken.Colon) return
    if (token is FormatToken.Equals || prev is FormatToken.Equals) return
    if (token is FormatToken.Op || prev is FormatToken.Op) return
    if (token is FormatToken.Comma || prev is FormatToken.Comma) return

    val out = ctx.out
    while (out.isNotEmpty() && out[out.length - 1] == ' ') {
        out.deleteCharAt(out.length - 1)
    }
    if (out.isEmpty() || out[out.length - 1] == '\n') return

    out.append(' ')
}
