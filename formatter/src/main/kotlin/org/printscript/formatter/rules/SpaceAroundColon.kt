package org.printscript.formatter.rules

class SpaceAroundColon : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Colon

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        val builder = ctx.out
        if (ctx.cfg.spaceBeforeColon) {
            builder.trimTrailingSpaces()
            if (builder.isNotEmpty() && builder.last() != '\n') {
                builder.append(' ')
            }
        }

        builder.append(':')

        if (ctx.cfg.spaceAfterColon) {
            builder.append(' ')
        }
    }
}

private fun StringBuilder.trimTrailingSpaces() {
    while (this.isNotEmpty() && this[this.length - 1] == ' ') {
        deleteCharAt(this.length - 1)
    }
}
