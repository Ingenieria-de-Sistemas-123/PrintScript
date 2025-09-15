package org.printscript.parser.node

import org.printscript.common.Position

data class LiteralNode<T>(
    val value: T,
) : ASTNode
