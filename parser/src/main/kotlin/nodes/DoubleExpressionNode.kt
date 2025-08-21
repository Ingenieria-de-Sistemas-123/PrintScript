package nodes

class DoubleExpressionNode(
    val left: ASTNode,
    val operator: String,
    val right: ASTNode,
) : ASTNode {
}