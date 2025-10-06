package org.printscript.cli.adapters

import org.printscript.formatter.CodeFormatter
import org.printscript.formatter.config.BraceStyle
import org.printscript.formatter.config.ConfigJsonReader
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.ASTNode

class FormatterAdapter {
    private val formatter = CodeFormatter()

    fun loadConfig(path: String?): FormatterConfig =
        if (path.isNullOrBlank()) {
            FormatterConfig(
                spaceAfterColon = true,
                spaceAroundEquals = true,
                lineJumpAfterSemicolon = true,
                braceStyle = BraceStyle.SAME_LINE,
            )
        } else {
            ConfigJsonReader().readFromFile(path)
        }

    fun format(
        ast: List<ASTNode>,
        cfg: FormatterConfig,
    ): String = formatter.format(ast, cfg)

    fun check(
        ast: List<ASTNode>,
        cfg: FormatterConfig,
        originalSource: String,
    ): String? {
        val formatted = format(ast, cfg)
        return if (formatted == originalSource) null else formatted
    }
}
