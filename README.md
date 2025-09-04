# CodeGentle

A Kotlin multiplatform library for generating Java/Kotlin source files.

> [!caution]
> WIP now.

## Installations

```gradle
repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        content {
            includeGroup("love.forte.codegentle")
            snapshotsOnly()
        }
    }

}

// snapshot only for NOW.
dependencies {
    implementation("love.forte.codegentle:codegentle-common:0.0.1-SNAPSHOT")
    implementation("love.forte.codegentle:codegentle-java:0.0.1-SNAPSHOT")
    implementation("love.forte.codegentle:codegentle-kotlin:0.0.1-SNAPSHOT")
    ksp("love.forte.codegentle:codegentle-kotlin-ksp:0.0.1-SNAPSHOT")
}

```
