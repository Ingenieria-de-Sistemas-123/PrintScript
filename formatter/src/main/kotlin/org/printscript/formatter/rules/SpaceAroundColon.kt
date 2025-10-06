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
