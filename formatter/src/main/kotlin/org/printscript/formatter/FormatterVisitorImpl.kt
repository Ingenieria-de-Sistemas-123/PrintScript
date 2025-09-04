package org.printscript.formatter

import node.ASTNode
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.interfaces.FormatToken


/**
 * ESTE ES EL PUNTO DONDE "USAMOS ÁRBOLES".
 *
 * Recorremos el AST (árbol) con Visitor y EMITIMOS tokens semánticos de formateo.
 * No escribimos texto final acá (salvo casos triviales); sólo describimos la estructura.
 * Luego RuleApplier+Reglas deciden los espacios concretos.
 */
class FormatterVisitorImpl(
    private val cfg: FormatterConfig,
    private val sink: MutableList<FormatToken> = mutableListOf()
) : Visitor<Unit> {

    /** Devuelve la secuencia de tokens de formateo resultante de recorrer el AST. */
    fun emit(node: ASTNode): List<FormatToken> {
        node.accept(this) // ← acá empieza el RECORRIDO DEL ÁRBOL
        return sink
    }

    // -------------------- Sentencias --------------------

    override fun visit(variableDeclarationStatement: VariableDeclarationStatement) {
        // let <id> : <type> = <expr> ;
        sink += Keyword(variableDeclarationStatement.getDeclarator()) // "let"
        // BAJO AL SUBÁRBOL: el identificador es un nodo hijo del AST
        sink += Ident(variableDeclarationStatement.getAssignmentExpression().getIdentifier().getIdentifier())
        sink += Colon
        // BAJO AL SUBÁRBOL de TypeDeclaration (no imprimimos texto directo)
        sink += TypeName(variableDeclarationStatement.getTypeDeclarationExpression().getType())
        sink += Equals
        variableDeclarationStatement.getAssignmentExpression().getValue().accept(this) // ← recorro subárbol expresión
        sink += Semicolon
    }

    override fun visit(assignmentStatement: AssignmentStatement) {
        // <id> = <expr> ;
        assignmentStatement.getIdentifier().accept(this) // ← recorro subárbol identificador
        sink += Equals
        assignmentStatement.getValue().accept(this) // ← recorro subárbol expresión
        sink += Semicolon
    }

    override fun visit(functionCallStatement: FunctionCallStatement) {
        // println(<args...>);
        repeat(cfg.lineJumpBeforePrintln) { sink += NewLine() }
        sink += Keyword(functionCallStatement.getFunctionName())
        sink += OpenParen
        functionCallStatement.getArguments().forEachIndexed { idx, arg ->
            arg.accept(this) // ← bajo al subárbol de cada argumento
            if (idx < functionCallStatement.getArguments().size - 1) sink += Comma
        }
        sink += CloseParen
        sink += Semicolon
    }

    override fun visit(ifStatement: IfStatement) {
        // if (<cond>) { \n  <then...> \n} [else { \n  <else...> \n}]
        sink += Keyword("if")
        sink += OpenParen
        ifStatement.getCondition().accept(this)   // ← subárbol condición
        sink += CloseParen

        sink += Keyword("{")
        sink += NewLine()
        ifStatement.getThenStatement().forEach {
            sink += Indent(cfg.indentSize)
            it.accept(this)                         // ← subárbol de cada sentencia del then
            sink += NewLine()
        }
        sink += Keyword("}")

        if (ifStatement.getElseStatement() != null) {
            sink += Keyword(" else ")
            sink += Keyword("{")
            sink += NewLine()
            ifStatement.getElseStatement()!!.forEach {
                sink += Indent(cfg.indentSize)
                it.accept(this)                       // ← subárbol de cada sentencia del else
                sink += NewLine()
            }
            sink += Keyword("}")
        }
    }

    // -------------------- Expresiones --------------------

    override fun visit(identifierExpression: IdentifierExpression) {
        sink += Ident(identifierExpression.getIdentifier())
    }

    override fun visit(numberLiteral: NumberLiteral) {
        sink += NumberLit(numberLiteral.getValue().toString())
    }

    override fun visit(stringLiteral: StringLiteral) {
        // OJO: guardamos el contenido sin comillas; las comillas se agregan en RuleApplier
        sink += StringLit(stringLiteral.getValue())
    }

    override fun visit(booleanLiteral: BooleanLiteral) {
        sink += Keyword(booleanLiteral.getValue().toString())
    }

    override fun visit(unaryExpression: UnaryExpression) {
        // Emitimos Op; la REGLA decidirá spacing/unario por contexto prev/next
        val op = when (unaryExpression.getOperator()) {
            "-"  -> Op(OpKind.MINUS)
            "+"  -> Op(OpKind.PLUS)
            else -> Op(OpKind.PLUS)
        }
        sink += op
        unaryExpression.getRight().accept(this) // ← subárbol operando
    }

    override fun visit(binaryExpression: BinaryExpression) {
        binaryExpression.getLeft().accept(this)
        val op = when (binaryExpression.getOperator()) {
            "+" -> Op(OpKind.PLUS)
            "-" -> Op(OpKind.MINUS)
            "*" -> Op(OpKind.STAR)
            "/" -> Op(OpKind.SLASH)
            else -> error("Operador no soportado: ${binaryExpression.getOperator()}")
        }
        sink += op
        binaryExpression.getRight().accept(this)
    }

    // Nodos no formateados por ahora
    override fun visit(readInputNode: ReadInputNode) = Unit
    override fun visit(readEnvNode: ReadEnvNode) = Unit
    override fun visit(typeDeclarationExpression: TypeDeclarationExpression) {
        sink += TypeName(typeDeclarationExpression.getType())
    }
}
