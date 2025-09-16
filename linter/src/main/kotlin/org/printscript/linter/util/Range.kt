package org.printscript.linter.util

import org.printscript.common.Position
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode

data class Range(val sl: Int, val sc: Int, val el: Int, val ec: Int)

private fun p(pos: Position) = Range(pos.line, pos.column, pos.line, pos.column)

fun rangeOf(node: ASTNode): Range =
    when (node) {
        is DeclarationNode -> p(node.position)
        is AssignationNode -> p(node.position)
        is PrintStatementNode -> p(node.position)
        is DoubleExpressionNode -> p(node.position)
        is LiteralNode<*> -> Range(1, 1, 1, 1)
        else -> Range(1, 1, 1, 1)
    }

fun idRange(
    name: String,
    pos: Position,
): Range = Range(pos.line, pos.column, pos.line, pos.column + name.length - 1)
