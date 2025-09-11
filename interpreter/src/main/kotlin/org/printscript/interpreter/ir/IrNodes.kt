package org.printscript.interpreter.ir

import org.printscript.interpreter.runtime.RType

// Sentencias (statements)
sealed interface StmtIR {
    fun <R> accept(v: StmtVisitor<R>): R
}

// v1.0
data class DeclIR(
    val name: String,
    val declaredType: RType,
    val initializer: ExprIR,
) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitDecl(this)
}

// v1.1
data class ConstDeclIR(
    val name: String,
    val declaredType: RType,
    val initializer: ExprIR,
) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitConstDecl(this)
}

data class AssignIR(val name: String, val expr: ExprIR) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitAssign(this)
}

data class PrintIR(val expr: ExprIR) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitPrint(this)
}

// v1.1 if/else
data class IfIR(
    val conditionVar: String,
    val thenBlock: List<StmtIR>,
    val elseBlock: List<StmtIR>?,
) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitIf(this)
}

// Expresiones
sealed interface ExprIR {
    fun <R> accept(v: ExprVisitor<R>): R
}

data class NumLit(val value: Double) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitNum(this)
}

data class StrLit(val value: String) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitStr(this)
}

// v1.1
data class BoolLit(val value: Boolean) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitBool(this)
}

data class IdRef(val name: String) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitId(this)
}

data class Binary(val op: Op, val left: ExprIR, val right: ExprIR) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitBinary(this)
}

enum class Op { PLUS, MINUS, STAR, SLASH }

// v1.1
data class ReadInputIR(
    val prompt: String,
    val expected: RType?,
) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitReadInput(this)
}

data class ReadEnvIR(
    val name: String,
    val expected: RType?,
) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitReadEnv(this)
}

// Visitors
interface ExprVisitor<R> {
    fun visitNum(n: NumLit): R

    fun visitStr(s: StrLit): R

    fun visitBool(b: BoolLit): R // v1.1

    fun visitId(i: IdRef): R

    fun visitBinary(b: Binary): R

    fun visitReadInput(r: ReadInputIR): R // v1.1

    fun visitReadEnv(r: ReadEnvIR): R // v1.1
}

interface StmtVisitor<R> {
    fun visitDecl(d: DeclIR): R

    fun visitConstDecl(c: ConstDeclIR): R // v1.1

    fun visitAssign(a: AssignIR): R

    fun visitPrint(p: PrintIR): R

    fun visitIf(i: IfIR): R // v1.1
}
