plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    testImplementation(project(":codegentle-common"))
    kspTest(project(":internal:enum-set"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
