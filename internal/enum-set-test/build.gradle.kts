plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    testImplementation(projects.codegentleCommon)
    kspTest(projects.internal.enumSet)
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
