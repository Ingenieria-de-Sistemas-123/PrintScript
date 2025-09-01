plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}
kotlin {
    jvmToolchain(21)
}
