package org.printscript.parser.node

import org.printscript.common.Position

class AssignationNode(
    val name: String,
    val type: ASTNode,
    val position: Position,
) : ASTNode
