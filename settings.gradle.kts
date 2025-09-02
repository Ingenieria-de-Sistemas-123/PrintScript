pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "2.1.10"
        id("com.diffplug.spotless") version "6.25.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "PrintScript2"

include(
    "analyzer",
    "cli",
    "formatter",
    "interpreter",
    "lexer",
    "parser",
    "token"
)
