package org.printscript.interpreter.adapter

import org.printscript.interpreter.ir.StmtIR
import org.printscript.parser.node.ASTNode

interface AstToIrMapper {
    fun transform(program: List<ASTNode>): List<StmtIR>
}
