package org.printscript.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.ConstantNode
import org.printscript.parser.node.ErrorNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.parser.node.VariableNode
import org.printscript.token.TokenType

class NodeTest {
    @Test
    fun construct_and_equals_print_statement() {
        val pos = Position(1, 5)
        val expr = LiteralNode("hi", TokenType.STRING)
        val node1 = PrintStatementNode(expr, pos)
        val node2 = PrintStatementNode(LiteralNode("hi", TokenType.STRING), Position(1, 5))

        assertEquals(expr, node1.expression)
        assertEquals(pos, node1.position)
        assertEquals(node1, node2)
        assertEquals(node1.hashCode(), node2.hashCode())
    }

    @Test
    fun not_equals_when_expression_differs_print_statement() {
        val pos = Position(1, 5)
        val a = PrintStatementNode(LiteralNode("hi", TokenType.STRING), pos)
        val b = PrintStatementNode(LiteralNode("bye", TokenType.STRING), pos)

        assertNotEquals((a.expression as LiteralNode<*>).value, (b.expression as LiteralNode<*>).value)
    }

    @Test
    fun construct_and_equals_constant_declaration() {
        val pos = Position(2, 3)
        val node = ConstantDeclarationNode("C", "number", LiteralNode(42, TokenType.NUMBER), pos)

        assertEquals("C", node.identifier)
        assertEquals("number", node.valueType)
        assertEquals(42, (node.expression as LiteralNode<*>).value)
        assertEquals(pos, node.position)
    }

    @Test
    fun not_equals_on_different_expression_constant_declaration() {
        val pos = Position(2, 3)
        val a = ConstantDeclarationNode("C", "number", LiteralNode(1, TokenType.NUMBER), pos)
        val b = ConstantDeclarationNode("C", "number", LiteralNode(2, TokenType.NUMBER), pos)
        assertNotEquals(a, b)
    }

    @Test
    fun construct_and_equals_variable_declaration() {
        val pos = Position(3, 1)
        val expr = LiteralNode("empty", TokenType.STRING)
        val a = VariableDeclarationNode("x", "string", expr, pos)
        val b = VariableDeclarationNode("x", "string", LiteralNode("empty", TokenType.STRING), Position(3, 1))

        assertEquals("x", a.identifier)
        assertEquals("string", a.valueType)
        assertEquals(expr, a.expression)
        assertEquals(pos, a.position)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun not_equals_on_type_change_variable_declaration() {
        val pos = Position(3, 1)
        val a = VariableDeclarationNode("x", "string", LiteralNode("empty", TokenType.STRING), pos)
        val b = VariableDeclarationNode("x", "number", LiteralNode("empty", TokenType.STRING), pos)
        assertNotEquals(a, b)
    }

    @Test
    fun construct_and_equals_constant_node() {
        val pos = Position(4, 2)
        val node = ConstantNode("FLAG", "boolean", LiteralNode(true, TokenType.BOOLEAN_TYPE), pos)

        assertEquals("FLAG", node.identifier)
        assertEquals("boolean", node.valueType)
        assertEquals(true, (node.expression as LiteralNode<*>).value)
        assertEquals(pos, node.position)
    }

    @Test
    fun not_equals_when_identifier_differs_constant_node() {
        val pos = Position(4, 2)
        val a = ConstantNode("A", "number", LiteralNode(1, TokenType.NUMBER), pos)
        val b = ConstantNode("B", "number", LiteralNode(1, TokenType.NUMBER), pos)
        assertNotEquals(a, b)
    }

    @Test
    fun construct_and_equals_variable() {
        val pos = Position(6, 7)
        val node = VariableNode("v", "number", LiteralNode(1, TokenType.NUMBER), pos)

        assertEquals("v", node.identifier)
        assertEquals("number", node.valueType)
        assertEquals(1, (node.expression as LiteralNode<*>).value)
        assertEquals(pos, node.position)
    }

    @Test
    fun not_equals_when_value_differs_variable() {
        val pos = Position(5, 7)
        val a = VariableNode("n", "number", LiteralNode(1, TokenType.NUMBER), pos)
        val b = VariableNode("n", "number", LiteralNode(2, TokenType.NUMBER), pos)
        assertNotEquals(a, b)
    }

    @Test
    fun holds_message_error() {
        val node = ErrorNode("boom")
        assertEquals("boom", node.error)
    }
}
