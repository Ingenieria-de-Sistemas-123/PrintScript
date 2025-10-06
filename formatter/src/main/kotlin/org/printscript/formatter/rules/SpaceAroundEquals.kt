package org.printscript.formatter.rules

class SpaceAroundEquals : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Equals

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        val layoutInfo = ctx.layout?.consume(t)
        // If the config explicitly required a policy for '=', enforce it regardless of layout.
        val explicit = ctx.cfg.spaceAroundEqualsExplicit
        if ((explicit ?: ctx.cfg.spaceAroundEquals)) {
            if (ctx.out.isNotEmpty() && ctx.out.last() != ' ' && ctx.out.last() != '\n') {
                ctx.out.append(' ')
            }
            ctx.out.append('=')
            ctx.out.append(' ')
        } else {
            if (layoutInfo != null) {
                ctx.out.append(layoutInfo.leading)
                ctx.out.append('=')
                ctx.out.append(layoutInfo.trailing)
            } else {
                ctx.out.append('=')
            }
        }
    }
}
