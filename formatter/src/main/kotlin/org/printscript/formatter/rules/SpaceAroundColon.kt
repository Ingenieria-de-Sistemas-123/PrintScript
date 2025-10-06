package org.printscript.formatter.rules

class SpaceAroundColon : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Colon

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        val builder = ctx.out
        val layoutInfo = ctx.layout?.consume(t)

        // Check for explicit requests first
        val explicitBefore = ctx.cfg.spaceBeforeColonExplicit
        val explicitAfter = ctx.cfg.spaceAfterColonExplicit

        val needsSpaceBefore = explicitBefore ?: ctx.cfg.spaceBeforeColon
        val needsSpaceAfter = explicitAfter ?: ctx.cfg.spaceAfterColon

        // If no explicit request and layout available, preserve original spacing
        if (explicitBefore == null && explicitAfter == null && !needsSpaceBefore && !needsSpaceAfter && layoutInfo != null) {
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
