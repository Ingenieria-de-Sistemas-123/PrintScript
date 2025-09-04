package org.printscript.formatter

import node.ASTNode
import org.printscript.formatter.config.FormatterConfig

/**
 * Fachada del formatter.
 * 1) RECIBE el ÁRBOL (AST) del parser.
 * 2) Lo recorre con un visitor para EMITIR tokens de formateo (semánticos).
 * 3) Aplica REGLAS sobre esos tokens para generar el string final.
 */
class CodeFormatter {
    fun format(node: ASTNode, config: FormatterConfig): String {
        // (1) AST → tokens
        val tokens = FormatterVisitorImpl(config).emit(node)
        // (2) tokens → reglas → string
        return RuleApplier(config).apply(tokens)
    }
}
