plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val kotlinVersion: String = libs.versions.kotlin.get()

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    // implementation(kotlin("serialization", kotlinVersion))
    // implementation(kotlin("power-assert", kotlinVersion))
    // compileOnly(kotlin("compiler", kotlinVersion))
    // compileOnly(kotlin("compiler-embeddable", kotlinVersion))
    implementation(libs.dokka.plugin)

    // see https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html#configure-the-project
    // see https://github.com/vanniktech/gradle-maven-publish-plugin
    // see https://plugins.gradle.org/plugin/com.vanniktech.maven.publish
    implementation(libs.maven.publish)

    implementation(libs.spotless)
}
