package org.printscript.interpreter.adapter

import org.printscript.interpreter.ir.StmtIR
import org.printscript.parser.node.ASTNode

class AstToIrV11 : AstToIrMapper {
    override fun transform(program: List<ASTNode>): List<StmtIR> {
        error("AstToIrV11 aún no implementado (Front-end 1.1 pendiente)")
    }
}
