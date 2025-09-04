plugins {
    // Use the Kotlin JVM plugin without specifying version
    // The version is inherited from the root project
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(libs.ksp)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
