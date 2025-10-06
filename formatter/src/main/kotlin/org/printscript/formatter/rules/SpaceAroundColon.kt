package org.printscript.formatter.rules

class SpaceAroundColon : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Colon

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        val builder = ctx.out
        val needsSpaceBefore = ctx.cfg.spaceBeforeColon
        val needsSpaceAfter = ctx.cfg.spaceAfterColon
        val layoutInfo = ctx.layout?.consume(t)

        if (!needsSpaceBefore && !needsSpaceAfter && layoutInfo != null) {
            builder.append(layoutInfo.leading)
            builder.append(':')
            builder.append(layoutInfo.trailing)
            return
        }
        builder.trimTrailingSpaces()
        if (needsSpaceBefore && builder.isNotEmpty() && builder.last() != '\n') {
            builder.append(' ')
        }

        builder.append(':')

        if (needsSpaceAfter) {
            builder.append(' ')
        }
    }
}
