package org.printscript.parser.node

data class LiteralNode<T>(
    val value: T,
) : ASTNode
