package org.printscript.interpreter.ir

import org.printscript.interpreter.runtime.RType

// sentencias
sealed interface StmtIR {
    fun <R> accept(v: StmtVisitor<R>): R
}

data class DeclIR(val name: String, val declaredType: RType, val initializer: ExprIR) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitDecl(this)
}

data class ConstDeclIR(val name: String, val declaredType: RType, val initializer: ExprIR) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitConstDecl(this)
}

data class AssignIR(val name: String, val expr: ExprIR) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitAssign(this)
}

data class PrintIR(val expr: ExprIR) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitPrint(this)
}

// expresiones
sealed interface ExprIR {
    fun <R> accept(v: ExprVisitor<R>): R
}

data class NumLit(val value: Double) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitNum(this)
}

data class StrLit(val value: String) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitStr(this)
}

data class IdRef(val name: String) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitId(this)
}

data class Binary(val op: Op, val left: ExprIR, val right: ExprIR) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitBinary(this)
}

data class ReadInput(val prompt: ExprIR) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitReadInput(this)
}

data class ReadEnv(val key: ExprIR) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitReadEnv(this)
}

data class IfIR(val condition: ExprIR, val thenBranch: List<StmtIR>, val elseBranch: List<StmtIR>?) : StmtIR {
    override fun <R> accept(v: StmtVisitor<R>): R = v.visitIf(this)
}

data class BoolLit(val value: Boolean) : ExprIR {
    override fun <R> accept(v: ExprVisitor<R>): R = v.visitBool(this)
}

enum class Op {
    PLUS,
    MINUS,
    STAR,
    SLASH,
}

// visitors
interface ExprVisitor<R> {
    fun visitNum(n: NumLit): R

    fun visitStr(s: StrLit): R

    fun visitId(i: IdRef): R

    fun visitBinary(b: Binary): R

    fun visitBool(b: BoolLit): R

    fun visitReadInput(r: ReadInput): R

    fun visitReadEnv(r: ReadEnv): R
}

interface StmtVisitor<R> {
    fun visitDecl(d: DeclIR): R

    fun visitAssign(a: AssignIR): R

    fun visitPrint(p: PrintIR): R

    fun visitIf(i: IfIR): R

    fun visitConstDecl(c: ConstDeclIR): R
}
