package nodes

class LiteralNode<T>(
    val value: T,
    val type: String,
) : ASTNode { }
