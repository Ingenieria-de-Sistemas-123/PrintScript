import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins { }

group = "org.printscript"
version = "1.0-SNAPSHOT"

allprojects {
    repositories { mavenCentral() }
}

tasks.register("installGitHooks") {
    group = "git"
    description = "Genera (si faltan) y copia los hooks pre-commit y pre-push a .git/hooks"

    doLast {
        val gitDir = file(".git")
        if (!gitDir.exists()) {
            println("⚠️  No se encontró .git/ en este directorio. ¿Ejecutaste `git init`?")
            return@doLast
        }

        val scriptsDir = file("gradle/scripts").apply { mkdirs() }

        val preCommitFile = scriptsDir.resolve("pre-commit")
        if (!preCommitFile.exists()) {
            preCommitFile.writeText(
                """
                |#!/usr/bin/env bash
                |set -euo pipefail
                |echo "[HOOK] Running pre-commit..."
                |./gradlew -q spotlessApply
                |./gradlew -q test
                |""".trimMargin() + "\n"
            )
        }

        val prePushFile = scriptsDir.resolve("pre-push")
        if (!prePushFile.exists()) {
            prePushFile.writeText(
                """
                |#!/usr/bin/env bash
                |set -euo pipefail
                |echo "[HOOK] Running pre-push..."
                |./gradlew -q check
                |""".trimMargin() + "\n"
            )
        }

        preCommitFile.setExecutable(true)
        prePushFile.setExecutable(true)

        val hooksDir = gitDir.resolve("hooks").apply { mkdirs() }
        val destPreCommit = hooksDir.resolve("pre-commit")
        val destPrePush = hooksDir.resolve("pre-push")

        Files.copy(preCommitFile.toPath(), destPreCommit.toPath(), StandardCopyOption.REPLACE_EXISTING)
        Files.copy(prePushFile.toPath(),   destPrePush.toPath(),   StandardCopyOption.REPLACE_EXISTING)

        destPreCommit.setExecutable(true)
        destPrePush.setExecutable(true)

        println("✅ Git hooks instalados/actualizados en .git/hooks")
        println("   - ${destPreCommit.absolutePath}")
        println("   - ${destPrePush.absolutePath}")
    }
}
