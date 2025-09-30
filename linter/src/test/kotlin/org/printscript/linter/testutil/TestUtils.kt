package org.printscript.linter.testutil

import org.printscript.common.Position
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.token.TokenType

object TestUtils {
    fun pos(
        l: Int,
        c: Int,
    ) = Position(l, c)

    fun num(n: Number) = LiteralNode(n, TokenType.NUMBER)

    fun str(s: String) = LiteralNode(s, TokenType.STRING)

    fun identifier(name: String) = LiteralNode(name, TokenType.IDENTIFIER)

    fun bool(b: Boolean) = LiteralNode(b, if (b) TokenType.TRUE else TokenType.FALSE)

    fun plus(
        l: ASTNode,
        r: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = DoubleExpressionNode(l, "+", r, pos(line, col))

    fun minus(
        l: ASTNode,
        r: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = DoubleExpressionNode(l, "-", r, pos(line, col))

    fun mul(
        l: ASTNode,
        r: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = DoubleExpressionNode(l, "*", r, pos(line, col))

    fun div(
        l: ASTNode,
        r: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = DoubleExpressionNode(l, "/", r, pos(line, col))

    fun printlnNode(
        expr: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = PrintStatementNode(expr, pos(line, col))

    fun readInput(
        expr: ASTNode,
        line: Int = 1,
        col: Int = 1,
    ) = ReadInputNode(expr, pos(line, col))

    fun declVar(
        name: String,
        type: String,
        expr: ASTNode,
        line: Int,
        col: Int,
    ): DeclarationNode = VariableDeclarationNode(name, type, expr, pos(line, col))

    fun declConst(
        name: String,
        type: String,
        expr: ASTNode,
        line: Int,
        col: Int,
    ): DeclarationNode = ConstantDeclarationNode(name, type, expr, pos(line, col))

    fun assign(
        name: String,
        expr: ASTNode,
        line: Int,
        col: Int,
    ) = AssignationNode(name, expr, pos(line, col))

    fun ifElse(
        ifNodes: List<ASTNode>,
        elseNodes: List<ASTNode>,
        condition: LiteralNode<*>,
    ) = IfElseNode(ifNodes, elseNodes, condition)
}
