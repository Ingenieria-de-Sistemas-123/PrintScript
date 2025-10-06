package org.printscript.formatter.rules

class SpaceAroundEquals : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Equals

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        val enforceSpacing = ctx.cfg.spaceAroundEquals || ctx.cfg.singleSpaceSeparation
        if (enforceSpacing) {
            if (ctx.out.isNotEmpty() && ctx.out.last() != ' ' && ctx.out.last() != '\n') {
                ctx.out.append(' ')
            }
            ctx.out.append('=')
            ctx.out.append(' ')
        } else {
            ctx.out.append('=')
        }
    }
}
