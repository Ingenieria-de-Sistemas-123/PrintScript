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
              "singleSpaceSeparation": true,
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
        assertEquals(true, cfg.singleSpaceSeparation)
        assertEquals(2, cfg.printLineBreaksAfter)
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
        assertEquals(false, cfg.singleSpaceSeparation)
        assertEquals(0, cfg.printLineBreaksAfter)
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
              "enforce-single-space-separation": true,
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
        assertEquals(true, cfg.singleSpaceSeparation)
        assertEquals(2, cfg.printLineBreaksAfter)
        assertEquals(false, cfg.lineJumpAfterSemicolon)
        assertEquals(2, cfg.indentSize)
        assertEquals(BraceStyle.NEXT_LINE, cfg.braceStyle)
    }

    @Test
    fun `interpreta print-x-line-breaks-after y if-indent-inside-x`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText(
            """
            {
              "print-0-line-breaks-after": true,
              "print-1-line-breaks-after": true,
              "print-2-line-breaks-after": true,
              "if-indent-inside-4": true,
              "if-indent-inside-8": false
            }
            """.trimIndent(),
        )

        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(2, cfg.printLineBreaksAfter) // Debe tomar el mayor valor true
        assertEquals(4, cfg.indentSize) // Solo el true
    }

    @Test
    fun `interpreta println-x-line-breaks-after`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText(
            """
            {
              "println-1-line-breaks-after": true
            }
            """.trimIndent(),
        )
        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(1, cfg.printLineBreaksAfter)
    }

    @Test
    fun `interpreta enforce-decl-spacing-before-colon y after-colon`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText(
            """
            {
              "enforce-decl-spacing-before-colon": true,
              "enforce-decl-spacing-after-colon": false
            }
            """.trimIndent(),
        )
        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(true, cfg.spaceBeforeColon)
        assertEquals(false, cfg.spaceAfterColon)
    }

    @Test
    fun `interpreta if-brace-same-line y if-brace-below-line`() {
        val tmp = Files.createTempFile("fmt-", ".json").toFile()
        tmp.writeText(
            """
            {
              "if-brace-same-line": true
            }
            """.trimIndent(),
        )
        val cfg = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(BraceStyle.SAME_LINE, cfg.braceStyle)

        tmp.writeText(
            """
            {
              "if-brace-below-line": true
            }
            """.trimIndent(),
        )
        val cfg2 = ConfigJsonReader().readFromFile(tmp.absolutePath)
        assertEquals(BraceStyle.NEXT_LINE, cfg2.braceStyle)
    }
}
