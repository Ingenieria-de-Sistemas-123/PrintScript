package org.printscript.interpreter.io

class SystemEnvProvider : EnvProvider {
    override fun get(name: String): String? = System.getenv(name)
}
