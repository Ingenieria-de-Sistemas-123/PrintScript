package org.printscript.linter.util

import org.printscript.common.Position
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode

data class Range(val sl: Int, val sc: Int, val el: Int, val ec: Int)

fun rangeOf(node: ASTNode): Range =
    when (node) {
        is DeclarationNode -> node.position.let { Range(it.line, it.column, it.line, it.column) }
        is AssignationNode -> node.position.let { Range(it.line, it.column, it.line, it.column) }
        is PrintStatementNode -> node.position.let { Range(it.line, it.column, it.line, it.column) }
        is LiteralNode<*> -> node.position.let { Range(it.line, it.column, it.line, it.column) }
        is DoubleExpressionNode -> node.position.let { Range(it.line, it.column, it.line, it.column) }
        else -> Range(1, 1, 1, 1)
    }

fun idRange(
    name: String,
    pos: Position,
): Range = Range(pos.line, pos.column, pos.line, pos.column + name.length - 1)
