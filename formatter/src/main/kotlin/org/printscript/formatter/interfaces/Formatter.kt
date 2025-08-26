package org.printscript.formatter.interfaces

import node.ASTNode
import org.printscript.formatter.config.FormatterConfig

interface Formatter {
    fun format(ast: List<ASTNode>, config: FormatterConfig = FormatterConfig()): String
}