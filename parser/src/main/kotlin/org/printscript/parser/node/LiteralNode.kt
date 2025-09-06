package org.printscript.parser.node

import org.printscript.common.Position

class LiteralNode<T>(
    val value: T,
    val type: String,
    val position: Position,
) : ASTNode
