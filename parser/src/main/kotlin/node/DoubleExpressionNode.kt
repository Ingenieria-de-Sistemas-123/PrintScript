package node

import org.printscript.common.Position

class DoubleExpressionNode(
    val left: ASTNode,
    val operator: String,
    val right: ASTNode,
    val position: Position,
) : ASTNode {
}