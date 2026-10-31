plugins {
    kotlin("jvm")
}

val kotlinVersion = version("kotlin")
val coroutinesVersion = version("kotlinx-coroutines")
val mcCoroutineVersion = version("mccoroutine")

group = "xyz.gmitch215.kotlinmc"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.235-stable")

    // Everything below resolves from the `Kotlin` plugin's classloader at runtime.
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    compileOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:${mcCoroutineVersion}")
}

fun version(name: String): String = File(rootDir.parentFile, "versions/${name}.txt").bufferedReader().use { it.readLine().trim() }
