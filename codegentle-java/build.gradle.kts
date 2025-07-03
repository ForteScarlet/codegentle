import com.google.devtools.ksp.gradle.KspAATask
import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    // Use the Kotlin Multiplatform plugin without specifying version
    // The version is inherited from the root project
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    // alias(libs.plugins.kotlinxBinaryCompatibilityValidator)
}

// apiValidation {
//     nonPublicMarkers.addAll(
//         listOf("love.forte.codepoet.java.InternalApi")
//     )
// }

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
            // Java
            "love.forte.codegentle.java.InternalJavaCodeGentleApi",
            "love.forte.codegentle.java.spec.CodeGentleJavaSpecImplementation"
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

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        binaries.library()
    }

    // Native targets
    // https://kotlinlang.org/docs/native-target-support.html
    // Tair1
    //// Apple macOS hosts only:
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64()

    // Tair2
    linuxX64()
    linuxArm64()
    //// Apple macOS hosts only:
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    // Tair3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    //// Apple macOS hosts only:
    watchosDeviceArm64()

    sourceSets {
        commonMain {
            kotlin.srcDir(project.layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
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

tasks.withType<KspAATask>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"

    val moduleName = "love.forte.codegentle.java"

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
