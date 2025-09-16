package org.printscript.parser.node

import org.printscript.common.Position

sealed interface DeclarationNode : ASTNode {
    val identifier: String
    val valueType: String
    val expression: ASTNode
    val position: Position
}
