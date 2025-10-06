package org.printscript.formatter.rules

class SpaceAroundOperator : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Op

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        t as FormatToken.Op
        val unaryMinus =
            t.kind == FormatToken.OpKind.MINUS &&
                (
                    ctx.prev == null ||
                        ctx.prev is FormatToken.OpenParen ||
                        ctx.prev is FormatToken.Equals ||
                        ctx.prev is FormatToken.Op ||
                        ctx.prev is FormatToken.Comma ||
                        ctx.prev is FormatToken.Colon
                )

        val symbol =
            when (t.kind) {
                FormatToken.OpKind.PLUS -> "+"
                FormatToken.OpKind.MINUS -> "-"
                FormatToken.OpKind.STAR -> "*"
                FormatToken.OpKind.SLASH -> "/"
            }

        if (!ctx.cfg.spaceAroundOperators || unaryMinus) {
            ctx.out.append(symbol)
            return
        }

        if (ctx.out.isNotEmpty() && ctx.out.last() != ' ' && ctx.out.last() != '\n') ctx.out.append(' ')
        ctx.out.append(symbol)
        ctx.out.append(' ')
    }
}
