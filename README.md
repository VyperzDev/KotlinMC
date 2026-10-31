# KotlinMC

A fork of [gmitch215/KotlinMC](https://github.com/gmitch215/KotlinMC) that serves as the **single, shared Kotlin runtime** for a Paper 26.3+ / Java 25 server (the one hosting PrisonTechCore's sub-plugins).

It is a legacy `plugin.yml` plugin named `Kotlin` whose only job is to fat-jar the Kotlin runtime, the kotlinx libraries, and MCCoroutine — **unrelocated** — so that:

- every plugin on the server shares one copy of the Kotlin classes (legacy plugins see each other's classloaders automatically; Paper plugins reach it via a `join-classpath` dependency),
- coroutine worker threads and MCCoroutine's plugin/session map live in a classloader that survives for the server's lifetime, instead of inside reloadable modules.

**This fork targets Paper 26.3+ on Java 25 only.** BungeeCord, Velocity, Sponge, and Modrinth publishing from the upstream project have been removed.

## Bundled artifacts

Each version is pinned by a file in `versions/`:

| Artifact | Version file |
| --- | --- |
| `org.jetbrains.kotlin:kotlin-stdlib` | `versions/kotlin.txt` |
| `org.jetbrains.kotlin:kotlin-reflect` | `versions/kotlin.txt` |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core` | `versions/kotlinx-coroutines.txt` |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | `versions/kotlinx-serialization.txt` |
| `org.jetbrains.kotlinx:atomicfu` | `versions/kotlinx-atomicfu.txt` |
| `org.jetbrains.kotlinx:kotlinx-io-core` | `versions/kotlinx-io.txt` |
| `org.jetbrains.kotlinx:kotlinx-datetime` | `versions/kotlinx-datetime.txt` |
| `com.github.shynixn.mccoroutine:mccoroutine-bukkit-api` | `versions/mccoroutine.txt` |
| `com.github.shynixn.mccoroutine:mccoroutine-bukkit-core` | `versions/mccoroutine.txt` |

Nothing is relocated. `META-INF/services` entries from coroutines and MCCoroutine are merged into the jar.

## For plugin authors

1. **Depend on the `Kotlin` plugin.**
   - Legacy `plugin.yml`:
     ```yaml
     depend: [ Kotlin ]
     ```
   - Paper `paper-plugin.yml` (needed to see a legacy plugin's classes):
     ```yaml
     dependencies:
       server:
         Kotlin:
           required: true
           load: BEFORE
           join-classpath: true
     ```
2. **Compile against the exact versions in `versions/` using `compileOnly`** (Gradle) / `provided` (Maven). Your module's Kotlin/coroutines/MCCoroutine versions must match this jar's `versions/*.txt`.
3. **Never shade Kotlin, kotlinx, or MCCoroutine into your own plugin.** The classes resolve from the `Kotlin` plugin's classloader at runtime; shading your own (or relocating) defeats the shared runtime and breaks MCCoroutine's lifecycle handling.

At boot, the plugin logs the bundled versions (`Bundled runtime: kotlin-stdlib ... , kotlinx-coroutines ... , kotlinx-serialization ... , mccoroutine ...`) so dependent plugins (e.g. PrisonTechCore) can verify they were compiled against the same versions.

## Building

```bash
./gradlew clean shadowJar
```

- Requires Gradle 9.3.1 (wrapper included).
- The build uses a **Java 25 toolchain**. The `org.gradle.toolchains.foojay-resolver-convention` plugin plus `org.gradle.java.installations.auto-download=true` in `gradle.properties` let Gradle download JDK 25 automatically if it is not installed locally.
- Only `Spigot.java` is compiled with Java 25; the bundled Kotlin jars remain JVM 8 bytecode, which is fine.
- Output: `build/libs/KotlinMC-<kotlin version>.jar` (e.g. `KotlinMC-2.3.10.jar`).

### Test plugin

`test-plugin/` contains a standalone one-file Kotlin plugin (not part of the main jar) that depends on `Kotlin`, compiles everything `compileOnly`, and on enable launches an MCCoroutine coroutine to prove stdlib, coroutines, and MCCoroutine all resolve from the `Kotlin` plugin's classloader:

```bash
./gradlew -p test-plugin build
```

Drop `test-plugin/build/libs/kotlinmc-test-plugin-1.0.0.jar` next to the KotlinMC jar; it logs `Job`'s classloader and `ok on <thread>` after a 20 ms delay.

## Version automation

Every hour, the GitHub Actions workflow checks these repositories for new releases:

- [`JetBrains/Kotlin`](https://github.com/JetBrains/Kotlin)
- [`Kotlin/kotlinx.coroutines`](https://github.com/Kotlin/kotlinx.coroutines)
- [`Kotlin/kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization)
- [`Kotlin/kotlinx-atomicfu`](https://github.com/Kotlin/kotlinx.atomicfu)
- [`Kotlin/kotlinx-io`](https://github.com/Kotlin/kotlinx-io)
- [`Kotlin/kotlinx-datetime`](https://github.com/Kotlin/kotlinx-datetime)
- [`Shynixn/MCCoroutine`](https://github.com/Shynixn/MCCoroutine)

If a new release is detected, the files in `versions/` are updated and committed, and CI rebuilds the jar with JDK 25.
