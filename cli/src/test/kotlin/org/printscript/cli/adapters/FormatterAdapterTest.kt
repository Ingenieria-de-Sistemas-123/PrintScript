import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.printscript.cli.adapters.FormatterAdapter
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.ASTNode
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FormatterAdapterTest {
    private val adapter = FormatterAdapter()

    @Test
    fun `loadConfig returns default when path is null`() {
        val cfg = adapter.loadConfig(null)
        assertEquals(4, cfg.indentSize) // valor por defecto de tu FormatterConfig
    }

    @Test
    fun `loadConfig returns default when path is blank`() {
        val cfg = adapter.loadConfig("")
        assertEquals(4, cfg.indentSize)
    }

    @Test
    fun `loadConfig reads from valid json file`(
        @TempDir tempDir: File,
    ) {
        val file = File(tempDir, "config.json")
        file.writeText(
            """{
              "spaceBeforeColon": true,
              "spaceAfterColon": false,
              "spaceAroundEquals": true,
              "spaceAroundOperators": true,
              "lineJumpAfterSemicolon": false,
              "lineJumpBeforePrintln": 0,
              "indentSize": 2
            }""",
        )

        val cfg = adapter.loadConfig(file.absolutePath)
        assertEquals(2, cfg.indentSize)
        assertEquals(true, cfg.spaceBeforeColon)
    }

    @Test
    fun `check returns null when already formatted`() {
        val ast = emptyList<ASTNode>() // o un mock simple
        val cfg = FormatterConfig()
        val src = "" // mismo que el resultado del formatter

        val result = adapter.check(ast, cfg, src)
        assertNull(result)
    }

    @Test
    fun `check returns formatted string when different`() {
        val ast = emptyList<ASTNode>() // idem, o mockear CodeFormatter
        val cfg = FormatterConfig()
        val src = "original"

        val result = adapter.check(ast, cfg, src)
        assertNotNull(result)
    }
}
