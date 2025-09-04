package org.printscript.formatter.rules

class SpaceAroundEquals : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Equals

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        if (ctx.cfg.spaceAroundEquals) ctx.out.append(" = ") else ctx.out.append("=")
    }
}
