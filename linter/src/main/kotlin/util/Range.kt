package org.printscript.range

import node.*
import org.printscript.common.Position

data class Range(val sl: Int, val sc: Int, val el: Int, val ec: Int)

fun rangeOf(node: ASTNode): Range = when (node) {
    is DeclarationNode -> node.position.let { Range(it.line, it.column, it.line, it.column) }
    is AssignationNode -> node.position.let { Range(it.line, it.column, it.line, it.column) }
    is PrintNode -> node.position.let { Range(it.line, it.column, it.line, it.column) }
    is LiteralNode<*> -> node.position.let { Range(it.line, it.column, it.line, it.column) }
    is DoubleExpressionNode  -> node.position.let { Range(it.line, it.column, it.line, it.column) }
    else -> Range(1,1,1,1)
}

fun idRange(name: String, pos: Position): Range =
    Range(pos.line, pos.column, pos.line, pos.column + name.length - 1)