package org.printscript.cli.adapters

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class FrontendAdapterVersionTest {
    @TempDir
    lateinit var temp: File

    @Test
    fun `parseProgram con version 1_1 y if else`() {
        val source =
            """
            let flag : boolean = true;
            if(flag){
              println("branchTrue");
            } else {
              println("branchFalse");
            }
            """.trimIndent()
        val file = temp.resolve("v11.ps").apply { writeText(source) }
        val fe = FrontendAdapter("1.1")
        val res = fe.parseProgram(file.readText(), file.path)
        assertTrue(res.isSuccess, "Debe parsear correctamente en 1.1")
        assertTrue(res.getOrThrow().isNotEmpty())
    }

    @Test
    fun `parseProgram con version no soportada falla temprano`() {
        val source = "let x : number = 1;"
        val file = temp.resolve("badver.ps").apply { writeText(source) }
        val fe = FrontendAdapter("9.9")
        val res = fe.parseProgram(file.readText(), file.path)
        assertTrue(res.isFailure, "Una versión no soportada debe producir failure")
        val failure = res.exceptionOrNull() as CliFailure
        assertTrue(failure.error.message.contains("Unsupported") || failure.error.message.contains("soportada"))
        // Al fallar temprano, no hay AST
    }
}
