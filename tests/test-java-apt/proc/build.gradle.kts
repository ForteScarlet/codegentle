plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        javaParameters = true
        freeCompilerArgs.addAll(
            "-Xcontext-parameters"
        )
    }
}

dependencies {
    api(project(":codegentle-common"))
    api(project(":codegentle-java"))
}
