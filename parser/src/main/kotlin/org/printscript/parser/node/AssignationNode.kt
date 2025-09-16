package org.printscript.parser.node

import org.printscript.common.Position

data class AssignationNode(
    val variable: String,
    val expression: ASTNode,
    val position: Position,
) : ASTNode
