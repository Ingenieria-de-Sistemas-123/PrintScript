package org.printscript.linter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.linter.issue.Issue
import org.printscript.linter.issue.Severity
import org.printscript.linter.rules.LintContext
import org.printscript.linter.testutil.TestUtils.assign
import org.printscript.linter.testutil.TestUtils.bool
import org.printscript.linter.testutil.TestUtils.declVar
import org.printscript.linter.testutil.TestUtils.div
import org.printscript.linter.testutil.TestUtils.minus
import org.printscript.linter.testutil.TestUtils.mul
import org.printscript.linter.testutil.TestUtils.num
import org.printscript.linter.testutil.TestUtils.plus
import org.printscript.linter.testutil.TestUtils.printlnNode
import org.printscript.linter.testutil.TestUtils.str
import org.printscript.linter.util.idRange
import org.printscript.linter.util.inferType
import org.printscript.linter.util.rangeOf
import org.printscript.parser.node.ASTNode

class UtilsTest {
    @Test
    fun `issue default severity WARNING and explicit ERROR`() {
        val i1 = Issue("r1", "msg", 1, 1, 1, 5)
        val i2 = Issue("r2", "msg2", 2, 3, 2, 10, Severity.ERROR)
        assertEquals(Severity.WARNING, i1.severity)
        assertEquals(Severity.ERROR, i2.severity)
    }

    @Test
    fun `lint context stores symbols and keeps config`() {
        val cfg = LintConfig(identifierStyle = IdentifierStyle.SNAKE_CASE)
        val ctx = LintContext(cfg)
        assertTrue(ctx.symbols.isEmpty())
        ctx.symbols["foo"] = "number"
        assertEquals("number", ctx.symbols["foo"])
        assertEquals(IdentifierStyle.SNAKE_CASE, ctx.config.identifierStyle)
    }

    @Test
    fun `idRange computes end column inclusive`() {
        val r = idRange("total", Position(3, 7))
        assertEquals(3, r.sl)
        assertEquals(7, r.sc)
        assertEquals(3, r.el)
        assertEquals(11, r.ec)
    }

    @Test
    fun `rangeOf covers supported node types`() {
        val d = declVar("x", "number", num(1), 5, 2)
        val a = assign("x", num(2), 6, 4)
        val e = plus(num(1), num(2), 7, 3)
        val p = printlnNode(str("hi"), 8, 5)
        val l = str("lit")

        val rd = rangeOf(d)
        val ra = rangeOf(a)
        val re = rangeOf(e)
        val rp = rangeOf(p)
        val rl = rangeOf(l)

        assertEquals(5, rd.sl)
        assertEquals(2, rd.sc)
        assertEquals(5, rd.el)
        assertEquals(2, rd.ec)
        assertEquals(6, ra.sl)
        assertEquals(4, ra.sc)
        assertEquals(6, ra.el)
        assertEquals(4, ra.ec)
        assertEquals(7, re.sl)
        assertEquals(3, re.sc)
        assertEquals(7, re.el)
        assertEquals(3, re.ec)
        assertEquals(8, rp.sl)
        assertEquals(5, rp.sc)
        assertEquals(8, rp.el)
        assertEquals(5, rp.ec)
        assertEquals(1, rl.sl)
        assertEquals(1, rl.sc)
        assertEquals(1, rl.el)
        assertEquals(1, rl.ec)
    }

    @Test
    fun `inferType handles literals, arithmetic and plus promotions`() {
        val ctx = LintContext(LintConfig())

        assertEquals("number", inferType(num(1), ctx))
        assertEquals("string", inferType(str("abc"), ctx))
        assertEquals("boolean", inferType(bool(true), ctx))

        assertEquals("number", inferType(plus(num(1), num(2)), ctx))

        assertEquals("string", inferType(plus(str("a"), num(1)), ctx))
        assertEquals("string", inferType(plus(num(1), str("a")), ctx))

        assertEquals("number", inferType(minus(num(3), num(1)), ctx))
        assertEquals("number", inferType(mul(num(3), num(1)), ctx))
        assertEquals("number", inferType(div(num(3), num(1)), ctx))

        assertNull(inferType(minus(str("a"), num(1)), ctx))
        assertNull(inferType(mul(str("a"), num(1)), ctx))
        assertNull(inferType(div(str("a"), num(1)), ctx))

        val asg: ASTNode = assign("x", plus(num(2), num(3)), 1, 1)
        assertEquals("number", inferType(asg, ctx))
    }
}
