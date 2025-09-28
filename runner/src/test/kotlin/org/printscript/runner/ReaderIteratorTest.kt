package org.printscript.runner

import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class ReaderIteratorTest {
    private val readerIterator = ReaderIterator()

    @Test
    fun `iterator yields every line in order`() {
        val input = "primera linea\nsegunda linea\ntercera linea\n"
        val iterator = readerIterator.getLineIterator(ByteArrayInputStream(input.toByteArray()))

        val collectedLines = mutableListOf<String>()
        while (iterator.hasNext()) {
            collectedLines += iterator.next()
        }

        assertEquals(listOf("primera linea", "segunda linea", "tercera linea"), collectedLines)
    }

    @Test
    fun `iterator reports no elements for an empty stream`() {
        val iterator = readerIterator.getLineIterator(ByteArrayInputStream(ByteArray(0)))

        assertFalse(iterator.hasNext())
    }

    @Test
    fun `calling next after exhaustion throws NoSuchElementException`() {
        val iterator = readerIterator.getLineIterator(ByteArrayInputStream("solo".toByteArray()))

        assertEquals("solo", iterator.next())
        assertFalse(iterator.hasNext())
        assertFailsWith<NoSuchElementException> { iterator.next() }
    }
}
