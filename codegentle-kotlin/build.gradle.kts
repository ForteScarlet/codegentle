
import com.google.devtools.ksp.gradle.KspAATask
import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    `module-maven-publish`
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
}

val squareLicenses = setOf(
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/Util.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/KotlinModifier.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/writer/KotlinCodeWriter.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/writer/LineWrapper.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/naming/KotlinClassName.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/naming/KotlinLambdaTypeName.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/spec/KotlinFunctionSpec.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/spec/KotlinParameterSpec.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/spec/KotlinPropertySpec.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/spec/KotlinTypealiasSpec.kt",
    "src/commonMain/kotlin/love/forte/codegentle/kotlin/spec/KotlinTypeSpec.kt",
)

spotless {
    kotlin {
        targetExclude(*squareLicenses.toTypedArray())
    }
    configSquareLicenseHeader(project, squareLicenses)
}

dependencies {
    kspCommonMainMetadata(project(":internal:enum-set"))
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

    applyDefaultHierarchyTemplate()

    jvmToolchain(11)
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
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
