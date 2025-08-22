package node

class LiteralNode<T>(
    val value: T,
    val type: String,
) : ASTNode { }
