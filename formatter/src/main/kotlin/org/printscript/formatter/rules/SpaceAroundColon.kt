package org.printscript.formatter.rules

class SpaceAroundColon : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Colon

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        val before = if (ctx.cfg.spaceBeforeColon) " " else ""
        val after = if (ctx.cfg.spaceAfterColon) " " else ""
        ctx.out.append(before).append(":").append(after)
    }
}
