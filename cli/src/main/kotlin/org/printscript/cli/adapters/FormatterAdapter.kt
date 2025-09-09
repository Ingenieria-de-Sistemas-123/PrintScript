package org.printscript.cli.adapters

import org.printscript.formatter.CodeFormatter
import org.printscript.formatter.config.ConfigJsonReader
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.ASTNode
import java.io.File

class FormatterAdapter {
    private val formatter = CodeFormatter()

    fun loadConfig(path: String?): FormatterConfig =
        if (path.isNullOrBlank()) {
            FormatterConfig()
        } else {
            ConfigJsonReader().readFromFile(File(path).readText())
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
