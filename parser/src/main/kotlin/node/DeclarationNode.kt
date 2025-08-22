package node

class DeclarationNode(
    val name: String,
    val type: String,
    val value: ASTNode,
) : ASTNode {
}