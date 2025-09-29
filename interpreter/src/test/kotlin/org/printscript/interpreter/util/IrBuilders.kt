package org.printscript.interpreter.util

import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.Binary
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.ExprIR
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.Op
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.RType

fun num(v: Double) = NumLit(v)

fun str(v: String) = StrLit(v)

fun id(name: String) = IdRef(name)

fun plus(
    l: ExprIR,
    r: ExprIR,
) = Binary(Op.PLUS, l, r)

fun minus(
    l: ExprIR,
    r: ExprIR,
) = Binary(Op.MINUS, l, r)

fun star(
    l: ExprIR,
    r: ExprIR,
) = Binary(Op.STAR, l, r)

fun slash(
    l: ExprIR,
    r: ExprIR,
) = Binary(Op.SLASH, l, r)

fun decl(
    name: String,
    type: RType,
    init: ExprIR?,
) = DeclIR(name, type, init)

fun assign(
    name: String,
    expr: ExprIR,
) = AssignIR(name, expr)

fun print(expr: ExprIR) = PrintIR(expr)
