import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.dokka)
}

val currentVersion = currentVersion()

allprojects {
    version = currentVersion
    group = "love.forte.codegentle"
    description = "A Kotlin multiplatform API for generating Java and Kotlin source code"
    repositories {
        mavenCentral()
    }
}

// region Dokka
subprojects {
    afterEvaluate {
        val p = this
        if (plugins.hasPlugin(libs.plugins.dokka.get().pluginId)) {
            dokka {
                configSourceSets(p)
                pluginsConfiguration.html {
                    configHtmlCustoms(p)
                }
            }
            rootProject.dependencies.dokka(p)
        }
    }
}

dokka {
    moduleName = "CodeGentle"

    dokkaPublications.all {
        if (isLocal()) {
            logger.info("Is local, offline")
            offlineMode = true
        }
    }

    configSourceSets(project)

    pluginsConfiguration.html {
        configHtmlCustoms(project)
    }
}
// endregion

// region Spotless
subprojects {
    afterEvaluate {
        if (plugins.hasPlugin(libs.plugins.spotless.get().pluginId)) {
            configure<SpotlessExtension> {
                configSpotless(this@afterEvaluate)
            }
        }
    }
}
// endregion
