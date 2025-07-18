plugins {
    java
}

dependencies {
    // 临时修复:
    compileOnly(projects.codegentleCommon)

    implementation(projects.tests.testJavaApt.proc)
    annotationProcessor(projects.tests.testJavaApt.proc)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
