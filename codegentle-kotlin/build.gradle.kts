import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    // Use the Kotlin Multiplatform plugin without specifying version
    // The version is inherited from the root project
    // id("org.jetbrains.kotlin.multiplatform")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    // alias(libs.plugins.kotlinxBinaryCompatibilityValidator)
}

dependencies {
    kspCommonMainMetadata(project(":internal:enum-set"))
}

kotlin {
    explicitApi()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.addAll(
            "love.forte.codegentle.common.codepoint.InternalCodePointApi",
            "love.forte.codegentle.common.InternalCommonCodeGentleApi",
            "love.forte.codegentle.common.naming.CodeGentleNamingImplementation",
            "love.forte.codegentle.common.ref.CodeGentleRefImplementation",
            "love.forte.codegentle.common.writer.CodeGentleCodeWriterImplementation",
            // Kotlin
            "love.forte.codegentle.kotlin.InternalKotlinCodeGentleApi",
            "love.forte.codegentle.kotlin.spec.CodeGentleKotlinSpecImplementation"
        )
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            withJvm()
            group("nonJvm") {
                withNative()

                group("jsBased") {
                    withJs()
                    withWasmJs()
                }
            }
        }
    }

    jvmToolchain(11)
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            javaParameters = true
            freeCompilerArgs.addAll("-Xjvm-default=all", "-Xjsr305=strict")
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js {
        nodejs()
        binaries.library()
    }


    sourceSets {
        commonMain {
            dependencies {
                api(project(":codegentle-common"))
            }

            tasks.withType<KspTaskMetadata> {
                kotlin.srcDir(destinationDirectory.file("kotlin"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"

    val moduleName = "love.forte.codegentle.kotlin"

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
