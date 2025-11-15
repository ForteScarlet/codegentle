plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    // 依赖 codegentle-kotlin-ksp 来使用转换功能
    implementation(project(":codegentle-kotlin-ksp"))

    // KSP API
    implementation(libs.ksp)
}

kotlin {
    compilerOptions {
        // 需要和 codegentle-kotlin-ksp 一样的 opt-in
        optIn.addAll(
            "love.forte.codegentle.common.codepoint.InternalCodePointApi",
            "love.forte.codegentle.common.InternalCommonCodeGentleApi",
            "love.forte.codegentle.common.naming.CodeGentleNamingImplementation",
            "love.forte.codegentle.common.ref.CodeGentleRefImplementation",
            "love.forte.codegentle.common.writer.CodeGentleCodeWriterImplementation",
            "love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation",
            "love.forte.codegentle.kotlin.InternalKotlinCodeGentleApi",
            "love.forte.codegentle.kotlin.spec.CodeGentleKotlinSpecImplementation",
            "love.forte.codegentle.kotlin.CodeGentleKotlinImportImplementation"
        )
    }

    jvmToolchain(11)
}
