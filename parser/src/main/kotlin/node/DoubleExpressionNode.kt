package node

class DoubleExpressionNode(
    val left: ASTNode,
    val operator: String,
    val right: ASTNode,
) : ASTNode {
}