package org.printscript.formatter

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.emit.ASTEmitter
import org.printscript.formatter.render.RuleApplier
import org.printscript.parser.node.ASTNode

class CodeFormatter {
    fun format(
        program: List<ASTNode>,
        config: FormatterConfig,
    ): String {
        val tokens = ASTEmitter(config).emitProgram(program)
        return RuleApplier(config).apply(tokens)
    }
}
