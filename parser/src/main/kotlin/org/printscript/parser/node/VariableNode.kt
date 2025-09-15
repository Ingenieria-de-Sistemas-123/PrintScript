package org.printscript.parser.node

import org.printscript.common.Position

class VariableNode(
    override val identifier: String,
    override val valueType: String,
    override val expression: ASTNode,
    override val position: Position
) : DeclarationNode