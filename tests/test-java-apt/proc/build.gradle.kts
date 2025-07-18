plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        javaParameters = true
        freeCompilerArgs.addAll(
            "-Xjvm-default=all",
            "-Xjsr305=strict",
        )
    }
}

dependencies {
    api(projects.codegentleCommon)
    api(projects.codegentleJava)
}
