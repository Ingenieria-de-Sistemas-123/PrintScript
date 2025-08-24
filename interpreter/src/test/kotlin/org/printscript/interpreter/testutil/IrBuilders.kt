package org.printscript.interpreter.testutil

import org.printscript.interpreter.ir.*
import org.printscript.interpreter.runtime.RType

fun num(v: Double) = NumLit(v)
fun str(v: String) = StrLit(v)
fun id(name: String) = IdRef(name)

fun plus(l: ExprIR, r: ExprIR) = Binary(Op.PLUS, l, r)
fun minus(l: ExprIR, r: ExprIR) = Binary(Op.MINUS, l, r)
fun star(l: ExprIR, r: ExprIR) = Binary(Op.STAR, l, r)
fun slash(l: ExprIR, r: ExprIR) = Binary(Op.SLASH, l, r)

fun decl(name: String, type: RType, init: ExprIR) = DeclIR(name, type, init)
fun assign(name: String, expr: ExprIR) = AssignIR(name, expr)
fun print(expr: ExprIR) = PrintIR(expr)
