package org.printscript.linter.testutil

import org.printscript.common.Position
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintNode

object AstFactory {
    fun pos(
        line: Int = 1,
        col: Int = 1,
    ) = Position(line, col)

    fun litNumber(
        v: String,
        line: Int = 1,
        col: Int = 1,
    ) = LiteralNode(v, "number", pos(line, col))

    fun litString(
        v: String,
        line: Int = 1,
        col: Int = 1,
    ) = LiteralNode(v, "string", pos(line, col))

    fun litIdentifier(
        name: String,
        line: Int = 1,
        col: Int = 1,
    ) = LiteralNode(name, "identifier", pos(line, col))

    fun decl(
        name: String,
        type: String,
        value: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = DeclarationNode(name, type, value, pos(line, col))

    fun assign(
        name: String,
        value: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = AssignationNode(name, value, pos(line, col))

    fun bin(
        left: ASTNode,
        op: String,
        right: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = DoubleExpressionNode(left, op, right, pos(line, col))

    fun print(
        expr: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = PrintNode(expr, pos(line, col))
}
