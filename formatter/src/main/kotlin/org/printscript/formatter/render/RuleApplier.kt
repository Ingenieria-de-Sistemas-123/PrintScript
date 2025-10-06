package org.printscript.formatter.render

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.layout.OriginalLayoutTracker
import org.printscript.formatter.rules.ApplyContext
import org.printscript.formatter.rules.CodeFormatRule
import org.printscript.formatter.rules.FormatToken

/**
 * Aplica reglas de formateo a una lista de tokens.
 * Las reglas pueden ser inyectadas para facilitar extensión y testing.
 * Para agregar una nueva regla, impleméntala y pásala en el constructor.
 */
class RuleApplier(
    private val config: FormatterConfig,
    private val fallbackRenderer: FormatTokenRenderer = DefaultTokenRenderer(),
    private val rules: List<CodeFormatRule> = DefaultRules.standard(fallbackRenderer),
) {
    fun apply(
        tokens: List<FormatToken>,
        layout: OriginalLayoutTracker? = null,
    ): String {
        val out = StringBuilder()
        for (index in tokens.indices) {
            val token = tokens[index]
            val ctx =
                ApplyContext(
                    prev = tokens.getOrNull(index - 1),
                    next = tokens.getOrNull(index + 1),
                    cfg = config,
                    out = out,
                    layout = layout,
                )
            val rule = rules.firstOrNull { it.matches(token) }
            if (rule != null) {
                rule.apply(token, ctx)
            } else {
                fallbackRenderer.render(token, ctx)
            }
        }
        return out.toString().trimEnd('\n')
    }
}
