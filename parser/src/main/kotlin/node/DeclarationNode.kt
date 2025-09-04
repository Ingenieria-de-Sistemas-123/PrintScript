package node

import org.printscript.common.Position

class DeclarationNode(
    val name: String,
    val type: String,
    val value: ASTNode,
    val position: Position,
) : ASTNode {
}