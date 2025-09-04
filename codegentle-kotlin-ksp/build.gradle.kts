
plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ksp)
    `module-maven-publish`
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
}

dependencies {
    api(project(":codegentle-common-ksp"))
    api(project(":codegentle-kotlin"))
    compileOnly(libs.ksp)
    testImplementation(kotlin("test"))
}

kotlin {
    explicitApi()
    compilerOptions {
        optIn.addAll(
            "love.forte.codegentle.common.codepoint.InternalCodePointApi",
            "love.forte.codegentle.common.InternalCommonCodeGentleApi",
            "love.forte.codegentle.common.naming.CodeGentleNamingImplementation",
            "love.forte.codegentle.common.ref.CodeGentleRefImplementation",
            "love.forte.codegentle.common.writer.CodeGentleCodeWriterImplementation",
            "love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation",
            // Kotlin
            "love.forte.codegentle.kotlin.InternalKotlinCodeGentleApi",
            "love.forte.codegentle.kotlin.spec.CodeGentleKotlinSpecImplementation",
            "love.forte.codegentle.kotlin.CodeGentleKotlinImportImplementation"
        )

        freeCompilerArgs.addAll(
            
            "-Xexpect-actual-classes",
            "-Xcontext-parameters"
        )
    }

    jvmToolchain(11)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"

    val moduleName = "love.forte.codegentle.kotlin.ksp"

    options.compilerArgumentProviders.add(
        CommandLineArgumentProvider {
            val sourceSet = sourceSets.findByName("main") ?: sourceSets.findByName("jvmMain")
            if (sourceSet != null) {
                // Provide compiled Kotlin classes to javac – needed for Java/Kotlin mixed sources to work
                listOf("--patch-module", "$moduleName=${sourceSet.output.asPath}")
            } else {
                emptyList()
            }
        }
    )
}
