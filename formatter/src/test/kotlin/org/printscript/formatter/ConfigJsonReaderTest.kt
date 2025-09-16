package org.printscript.formatter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.formatter.config.ConfigJsonReader
import java.nio.file.Files

class ConfigJsonReaderTest {
    @Test
    fun `lee JSON completo`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText(
            """
            {
              "spaceBeforeColon": true,
              "spaceAfterColon": false,
              "spaceAroundEquals": false,
              "spaceAroundOperators": true,
              "lineJumpBeforePrintln": 2,
              "lineJumpAfterSemicolon": false,
              "indentSize": 2
            }
            """.trimIndent(),
        )

        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(true, cfg.spaceBeforeColon)
        assertEquals(false, cfg.spaceAfterColon)
        assertEquals(false, cfg.spaceAroundEquals)
        assertEquals(true, cfg.spaceAroundOperators)
        assertEquals(2, cfg.lineJumpBeforePrintln)
        assertEquals(false, cfg.lineJumpAfterSemicolon)
        assertEquals(2, cfg.indentSize)
    }

    @Test
    fun `valores faltantes toman defaults`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText("""{ "spaceAroundEquals": true, "indentSize": 8 }""")

        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(false, cfg.spaceBeforeColon)
        assertEquals(true, cfg.spaceAfterColon)
        assertEquals(true, cfg.spaceAroundEquals)
        assertEquals(true, cfg.spaceAroundOperators)
        assertEquals(0, cfg.lineJumpBeforePrintln)
        assertEquals(true, cfg.lineJumpAfterSemicolon)
        assertEquals(8, cfg.indentSize)
    }
}
