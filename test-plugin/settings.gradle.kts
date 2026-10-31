pluginManagement {
    val kotlinVersion = File(rootDir, "../versions/kotlin.txt").readText().trim()
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kotlinmc-test-plugin"
