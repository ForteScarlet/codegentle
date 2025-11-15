plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ksp)
}

dependencies {
    // 依赖处理器模块 - 注解定义
    implementation(project(":tests:test-ksp:proc"))

    // KSP 处理器
    ksp(project(":tests:test-ksp:proc"))

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// 配置 KSP 生成的代码目录
kotlin.sourceSets.main {
    kotlin.srcDir("build/generated/ksp/main/kotlin")
}
