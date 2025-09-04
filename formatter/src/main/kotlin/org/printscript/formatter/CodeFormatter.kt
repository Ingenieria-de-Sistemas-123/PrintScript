package org.printscript.formatter

import node.ASTNode
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.emit.AstEmitter
import org.printscript.formatter.render.RuleApplier

/**
 * 1) AST (del parser) -> tokens de formateo
 * 2) Reglas -> string final
 */
class CodeFormatter {
    fun format(program: List<ASTNode>, config: FormatterConfig): String {
        val tokens = AstEmitter(config).emitProgram(program)
        return RuleApplier(config).apply(tokens)
    }
}
