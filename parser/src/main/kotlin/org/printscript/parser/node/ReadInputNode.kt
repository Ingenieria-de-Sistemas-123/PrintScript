package org.printscript.parser.node

import org.printscript.common.Position

data class ReadInputNode(
    val expression: ASTNode,
    val position: Position
) : ASTNode