package org.printscript.formatter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.formatter.config.BraceStyle
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
              "indentSize": 2,
              "braceStyle": "next_line"
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
        assertEquals(BraceStyle.NEXT_LINE, cfg.braceStyle)
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
        assertEquals(BraceStyle.SAME_LINE, cfg.braceStyle)
    }

    @Test
    fun `brace style interpreta alias`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText(
            """
            {
              "braceOnNewLine": true,
              "ifBraceSameLine": false
            }
            """.trimIndent(),
        )

        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(BraceStyle.NEXT_LINE, cfg.braceStyle)
    }

    @Test
    fun `interpreta alias de TCK`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText(
            """
            {
              "mandatory-single-space-separation": true,
              "enforce-spacing-before-colon-in-declaration": true,
              "enforce-spacing-after-colon-in-declaration": false,
              "enforce-no-spacing-around-equals": true,
              "mandatory-space-surrounding-operations": false,
              "line-breaks-after-println": 2,
              "mandatory-line-break-after-statement": false,
              "indent-inside-if": 2,
              "if-brace-below-line": true
            }
            """.trimIndent(),
        )

        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(true, cfg.spaceBeforeColon)
        assertEquals(false, cfg.spaceAfterColon)
        assertEquals(false, cfg.spaceAroundEquals)
        assertEquals(false, cfg.spaceAroundOperators)
        assertEquals(2, cfg.lineJumpBeforePrintln)
        assertEquals(false, cfg.lineJumpAfterSemicolon)
        assertEquals(2, cfg.indentSize)
        assertEquals(BraceStyle.NEXT_LINE, cfg.braceStyle)
    }
}
