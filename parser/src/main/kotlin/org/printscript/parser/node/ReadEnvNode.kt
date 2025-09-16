package org.printscript.parser.node

import org.printscript.common.Position

data class ReadEnvNode(
    val expression: ASTNode,
    val position: Position,
) : ASTNode
