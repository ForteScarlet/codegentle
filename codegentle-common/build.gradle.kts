import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    // Use the Kotlin Multiplatform plugin without specifying version
    // The version is inherited from the root project
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.spotless)
    `module-maven-publish`
    alias(libs.plugins.dokka)
}

val squareLicenses = setOf(
    "src/commonMain/kotlin/love/forte/codegentle/common/naming/TypeName.kt",
    "src/commonMain/kotlin/love/forte/codegentle/common/naming/ArrayTypeName.kt",
    "src/commonMain/kotlin/love/forte/codegentle/common/naming/ParameterizedTypeName.kt",
    "src/commonMain/kotlin/love/forte/codegentle/common/naming/TypeVariableName.kt",
    "src/commonMain/kotlin/love/forte/codegentle/common/naming/WildcardTypeName.kt",
    "src/commonMain/kotlin/love/forte/codegentle/common/writer/CodeWriter.kt",
    "src/commonMain/kotlin/love/forte/codegentle/common/Util.kt",
)

spotless {
    kotlin {
        targetExclude(*(squareLicenses + googleLicences).toTypedArray())
    }
    configGoogleLicenseHeader(project)
    configSquareLicenseHeader(project, squareLicenses)
}

kotlin {
    explicitApi()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.addAll(
            "love.forte.codegentle.common.InternalCommonCodeGentleApi",
            "love.forte.codegentle.common.codepoint.InternalCodePointApi",
            "love.forte.codegentle.common.naming.CodeGentleNamingImplementation",
            "love.forte.codegentle.common.ref.CodeGentleRefImplementation",
            "love.forte.codegentle.common.writer.CodeGentleCodeWriterImplementation",
            "love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation",
        )
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xcontext-parameters"
        )
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
        compilerOptions {
            javaParameters = true
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

dependencies {
    kspCommonMainMetadata(project(":internal:code-value-extensions"))
    kspCommonMainMetadata(project(":internal:enum-set"))
}

// https://github.com/google/ksp/issues/963#issuecomment-2919262595
tasks.withType<KspAATask>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.sourcesJar.configure {
    dependsOn("kspCommonMainKotlinMetadata")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "11"
    targetCompatibility = "11"

    val moduleName = "love.forte.codegentle.common"

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
