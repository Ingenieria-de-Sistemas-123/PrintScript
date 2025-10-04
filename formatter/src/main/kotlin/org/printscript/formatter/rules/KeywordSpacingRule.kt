package org.printscript.formatter.rules

/**
 * Inserta un espacio automático tras palabras clave que lo requieren cuando el
 * emisor no lo introduce explícitamente en el stream de tokens.
 */
class KeywordSpacingRule(
    private val keywordsWithSpace: Set<String> = setOf("let"),
) : CodeFormatRule {
    override fun matches(t: FormatToken) = t is FormatToken.Keyword && t.text in keywordsWithSpace

    override fun apply(
        t: FormatToken,
        ctx: ApplyContext,
    ) {
        t as FormatToken.Keyword
        ctx.out.append(t.text)
        val needsSpace =
            ctx.next != null &&
                ctx.next !is FormatToken.Space &&
                ctx.next !is FormatToken.NewLine &&
                ctx.out.lastOrNull() != ' '
        if (needsSpace) {
            ctx.out.append(' ')
        }
    }
}

private fun StringBuilder.lastOrNull(): Char? = if (isEmpty()) null else this[length - 1]
