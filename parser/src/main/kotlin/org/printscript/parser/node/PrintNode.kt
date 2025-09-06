package org.printscript.parser.node

import org.printscript.common.Position

class PrintNode(
    val expression: ASTNode,
    val position: Position,
) : ASTNode
