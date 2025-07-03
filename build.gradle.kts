plugins {
    // Apply Kotlin plugins to the root project with 'apply false'
    // This makes the plugins available to subprojects without applying them to the root project
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    id("org.jetbrains.dokka")
}

val currentVersion = currentVersion()

allprojects {
    version = currentVersion
    group = "love.forte.codegentle"
    description = "A Kotlin multiplatform API for generating Java source code"
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
