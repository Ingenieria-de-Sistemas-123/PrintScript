plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "PrintScript2"
include("lexer")
include("interpreter")
include("parser")
include("formatter")
include("cli")
include("linter")
include("token")
include("common")
include("common")
