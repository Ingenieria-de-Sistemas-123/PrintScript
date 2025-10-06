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

        // When there's an explicit request, ALWAYS enforce it
        when {
            explicitBefore != null && explicitAfter != null -> {
                // Both before and after are explicitly set
                builder.trimTrailingSpaces()
                if (explicitBefore && builder.isNotEmpty() && builder.last() != '\n') {
                    builder.append(' ')
                }
                builder.append(':')
                if (explicitAfter) {
                    builder.append(' ')
                }
            }
            explicitBefore != null -> {
                // Only before is explicitly set
                builder.trimTrailingSpaces()
                if (explicitBefore && builder.isNotEmpty() && builder.last() != '\n') {
                    builder.append(' ')
                }
                builder.append(':')
                // For after, preserve layout if available, otherwise use default
                if (layoutInfo != null) {
                    builder.append(layoutInfo.trailing)
                } else if (ctx.cfg.spaceAfterColon) {
                    builder.append(' ')
                }
            }
            explicitAfter != null -> {
                // Only after is explicitly set
                // For before, preserve layout if available, otherwise use default
                if (layoutInfo != null) {
                    builder.append(layoutInfo.leading)
                } else {
                    builder.trimTrailingSpaces()
                    if (ctx.cfg.spaceBeforeColon && builder.isNotEmpty() && builder.last() != '\n') {
                        builder.append(' ')
                    }
                }
                builder.append(':')
                if (explicitAfter) {
                    builder.append(' ')
                }
            }
            layoutInfo != null -> {
                // No explicit requests but have layout - check if config differs from defaults
                // If config has non-default values, use them; otherwise preserve layout
                val hasNonDefaultConfig = ctx.cfg.spaceBeforeColon || ctx.cfg.spaceAfterColon
                if (hasNonDefaultConfig) {
                    // Use config values instead of preserving layout
                    builder.trimTrailingSpaces()
                    if (ctx.cfg.spaceBeforeColon && builder.isNotEmpty() && builder.last() != '\n') {
                        builder.append(' ')
                    }
                    builder.append(':')
                    if (ctx.cfg.spaceAfterColon) {
                        builder.append(' ')
                    }
                } else {
                    // Preserve original layout
                    builder.append(layoutInfo.leading)
                    builder.append(':')
                    builder.append(layoutInfo.trailing)
                }
            }
            else -> {
                // No explicit requests and no layout - use default behavior
                builder.trimTrailingSpaces()
                if (ctx.cfg.spaceBeforeColon && builder.isNotEmpty() && builder.last() != '\n') {
                    builder.append(' ')
                }
                builder.append(':')
                if (ctx.cfg.spaceAfterColon) {
                    builder.append(' ')
                }
            }
        }
    }
}
