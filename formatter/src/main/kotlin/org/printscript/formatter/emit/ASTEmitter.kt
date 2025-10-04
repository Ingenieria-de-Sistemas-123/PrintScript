package org.printscript.formatter.emit

import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.ASTNode

class ASTEmitter private constructor(
    private val statements: StatementEmitter,
) {
    constructor(config: FormatterConfig) : this(StatementEmitter(config, ExpressionEmitter()))

    fun emitProgram(program: List<ASTNode>) = statements.emitProgram(program)
}
