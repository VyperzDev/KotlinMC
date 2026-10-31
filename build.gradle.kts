plugins {
    java
    id("com.gradleup.shadow") version "9.0.2"
}

val kotlinVersion = version("kotlin")
val coroutinesVersion = version("kotlinx-coroutines")
val serializationVersion = version("kotlinx-serialization")
val atomicfuVersion = version("kotlinx-atomicfu")
val kotlinIoVersion = version("kotlinx-io")
val dateTimeVersion = version("kotlinx-datetime")
val mcCoroutineVersion = version("mccoroutine")

group = "xyz.gmitch215.kotlinmc"
version = kotlinVersion
description = "A Library Jar containing various Kotlin Dependencies for Minecraft Plugins."

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationVersion}")
    implementation("org.jetbrains.kotlinx:atomicfu:${atomicfuVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:${kotlinIoVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${dateTimeVersion}")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:${mcCoroutineVersion}")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:${mcCoroutineVersion}")

    compileOnly("io.papermc.paper:paper-api:26.3.build.235-stable")
}

tasks {
    jar.configure {
        enabled = false
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier = ""

        // kotlinx-coroutines and MCCoroutine both register providers under
        // META-INF/services; merge the entries instead of letting one jar's
        // files overwrite the other's.
        mergeServiceFiles()

        // Intentionally NO relocation: this jar IS the canonical, shared copy of
        // the Kotlin runtime and MCCoroutine for the whole server. Every other
        // plugin compiles against the original package names (kotlin.*,
        // kotlinx.*, com.github.shynixn.mccoroutine.*) and resolves them from
        // this plugin's classloader at runtime, so the packages must stay put.

        dependencies {
            exclude(dependency("org.jetbrains:annotations"))
        }
    }

    processResources {
        expand(
            "kotlin_version" to kotlinVersion,
            "coroutines_version" to coroutinesVersion,
            "serialization_version" to serializationVersion,
            "mccoroutine_version" to mcCoroutineVersion,
            "description" to (project.description ?: "")
        )
    }
}

fun version(name: String): String = File("versions/${name}.txt").bufferedReader().use { it.readLine().trim() }
