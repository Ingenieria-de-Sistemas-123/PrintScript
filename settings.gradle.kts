plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "PrintScript2"
include("interpreter")
include("parser")
include("lexer")
include("formatter")
include("lexer")
include("interpreter")
include("parser")
include("formatter")
include("cli")
include("analyzer")
include("token")
