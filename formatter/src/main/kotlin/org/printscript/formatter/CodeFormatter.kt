package org.printscript.formatter

import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.emit.ASTEmitter
import org.printscript.formatter.render.RuleApplier
import org.printscript.parser.node.ASTNode

class CodeFormatter(
    private val emitterFactory: (FormatterConfig) -> ASTEmitter = { cfg -> ASTEmitter(cfg) },
    private val ruleApplierFactory: (FormatterConfig) -> RuleApplier = { cfg -> RuleApplier(cfg) },
) {
    fun format(
        program: List<ASTNode>,
        config: FormatterConfig,
    ): String {
        val tokens = emitterFactory(config).emitProgram(program)
        return ruleApplierFactory(config).apply(tokens)
    }
}
