plugins {
    signing
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

val p = project

mavenPublishing {
    publishToMavenCentral()

    if (!isLocal()) {
        signAllPublications()
    }

    pom {
        name = p.name
        description = p.description
        url = P.HOMEPAGE
        inceptionYear = "2024"

        licenses {
            license {
                // https://spdx.org/licenses/
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        scm {
            url = "https://github.com/ForteScarlet/codegentle"
            connection = "scm:git:https://github.com/ForteScarlet/codegentle.git"
            developerConnection = "scm:git:ssh://git@github.com/ForteScarlet/codegentle.git"
        }

        developers {
            developer {
                id = "forte"
                name = "ForteScarlet"
                email = "ForteScarlet@163.com"
                url = "https://github.com/ForteScarlet"
            }
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/ForteScarlet/codegentle/issues")
        }

    }
}
