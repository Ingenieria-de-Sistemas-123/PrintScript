import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    kotlin("jvm")
    jacoco
    id("com.diffplug.spotless")
    `maven-publish`
}

repositories { mavenCentral() }

// === Toolchains (Java + Kotlin) para cada proyecto que use este plugin
extensions.configure<JavaPluginExtension> {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}
extensions.configure<KotlinJvmProjectExtension> {
    jvmToolchain(21)
}

// === Spotless (solo check; no formatea en 'check')
configure<SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(2)
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

// === Tests (JUnit Platform) + reporte de cobertura
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

configure<JacocoPluginExtension> { toolVersion = "0.8.11" }

// ---- jacocoTestReport: registrar si no existe, si existe, solo configurar
if (!tasks.names.contains("jacocoTestReport")) {
    tasks.register("jacocoTestReport", JacocoReport::class) {
        dependsOn("test")
        reports { xml.required.set(true); html.required.set(true) }
        val bdir = layout.buildDirectory.get().asFile
        executionData.setFrom(
            fileTree(bdir).include(
                "jacoco/test.exec",
                "jacoco/test*.exec",
                "outputs/unit_test_code_coverage/*/*.ec"
            )
        )
    }
} else {
    tasks.named("jacocoTestReport", JacocoReport::class).configure {
        reports { xml.required.set(true); html.required.set(true) }
        val bdir = layout.buildDirectory.get().asFile
        executionData.setFrom(
            fileTree(bdir).include(
                "jacoco/test.exec",
                "jacoco/test*.exec",
                "outputs/unit_test_code_coverage/*/*.ec"
            )
        )
    }
}

// ---- jacocoTestCoverageVerification: registrar si no existe, si existe, configurar
if (!tasks.names.contains("jacocoTestCoverageVerification")) {
    tasks.register("jacocoTestCoverageVerification", JacocoCoverageVerification::class) {
        dependsOn("test")
        violationRules { rule { limit { minimum = "0.80".toBigDecimal() } } }
        val bdir = layout.buildDirectory.get().asFile
        executionData.setFrom(
            fileTree(bdir).include(
                "jacoco/test.exec",
                "jacoco/test*.exec",
                "outputs/unit_test_code_coverage/*/*.ec"
            )
        )
    }
} else {
    tasks.named("jacocoTestCoverageVerification", JacocoCoverageVerification::class).configure {
        violationRules { rule { limit { minimum = "0.80".toBigDecimal() } } }
        val bdir = layout.buildDirectory.get().asFile
        executionData.setFrom(
            fileTree(bdir).include(
                "jacoco/test.exec",
                "jacoco/test*.exec",
                "outputs/unit_test_code_coverage/*/*.ec"
            )
        )
    }
}

// ---- check orquesta calidad
tasks.named("check") {
    dependsOn("spotlessCheck", "jacocoTestReport", "jacocoTestCoverageVerification")
}

// === Publicación Maven para todos los módulos ===
plugins.withId("maven-publish") {
    extensions.configure<org.gradle.api.publish.PublishingExtension> {
        publications {
            create("maven", org.gradle.api.publish.maven.MavenPublication::class.java) {
                from(components["java"])
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/Ingenieria-de-Sistemas-123/PrintScript")
                credentials {
                    username = System.getenv("GITHUB_ACTOR") ?: ""
                    password = System.getenv("GITHUB_TOKEN") ?: ""
                }
            }
        }
    }
}
