package org.printscript.linter.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.linter.testutil.AstFactory

class RangeTest {
    @Test
    fun rangeOf_cubriendo_todos_los_nodos_conocidos() {
        val d = AstFactory.decl("a", "number", AstFactory.litNumber("0"), 2, 4)
        val a = AstFactory.assign("a", AstFactory.litNumber("1"), 3, 2)
        val p = AstFactory.print(AstFactory.litString("\"x\""), 4, 1)
        val l = AstFactory.litIdentifier("id", 5, 7)
        val e = AstFactory.bin(AstFactory.litNumber("1"), "+", AstFactory.litNumber("2"), 6, 9)

        val rd = rangeOf(d)
        assertEquals(Range(2, 4, 2, 4), rd)
        val ra = rangeOf(a)
        assertEquals(Range(3, 2, 3, 2), ra)
        val rp = rangeOf(p)
        assertEquals(Range(4, 1, 4, 1), rp)
        val rl = rangeOf(l)
        assertEquals(Range(5, 7, 5, 7), rl)
        val re = rangeOf(e)
        assertEquals(Range(6, 9, 6, 9), re)
    }

    @Test
    fun idRange_basico_y_longitud_correcta() {
        val pos = Position(10, 3)
        val r = idRange("foobar", pos)
        // endCol = startCol + len - 1 => 3 + 6 - 1 = 8
        assertEquals(Range(10, 3, 10, 8), r)
    }
}
