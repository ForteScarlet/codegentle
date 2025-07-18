plugins {
    java
}

dependencies {
    implementation(project(":tests:test-java-apt:proc"))
    annotationProcessor(project(":tests:test-java-apt:proc"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
