package org.printscript.formatter.implementation

import node.ASTNode
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.interfaces.Formatter

class DefaultFormatter: Formatter {
    override fun format(
        ast: List<ASTNode>,
        config: FormatterConfig
    ): String {
        TODO("Not yet implemented")
    }


}