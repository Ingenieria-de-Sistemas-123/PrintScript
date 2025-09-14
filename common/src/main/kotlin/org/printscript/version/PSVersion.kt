package org.printscript.version

enum class PSVersion { V1_0, V1_1 }

fun parsePSVersion(s: String): PSVersion =
    when (s.trim()) {
        "1.0" -> PSVersion.V1_0
        "1.1" -> PSVersion.V1_1
        else -> error("Unsupported PrintScript version: $s (use 1.0 or 1.1)")
    }
