package org.printscript.parser.node

import org.printscript.common.Position

data class ConstantDeclarationNode(
    override val identifier: String,
    override val valueType: String,
    override val expression: ASTNode,
    override val position: Position
) : DeclarationNode {
    override fun equals(other: Any?): Boolean {
        return when {
            other is VariableDeclarationNode -> {
                this.identifier == other.identifier &&
                        this.valueType == other.valueType &&
                        this.expression == other.expression
            }
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = identifier.hashCode()
        result = 31 * result + valueType.hashCode()
        result = 31 * result + expression.hashCode()
        result = 31 * result + position.hashCode()
        return result
    }
}