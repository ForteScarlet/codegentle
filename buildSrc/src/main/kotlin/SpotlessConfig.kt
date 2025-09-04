import com.diffplug.gradle.spotless.KotlinExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project


val googleLicences = setOf(
    "src/*Main/kotlin/love/forte/codegentle/common/naming/ClassName.kt",
    "src/*Main/kotlin/love/forte/codegentle/common/naming/ClassName.*.kt",
)

fun SpotlessExtension.configSpotless(project: Project) {
    val root = project.rootProject

    // basic Kotlin source files' license header
    kotlin {
        target("src/**/*.kt")
        licenseHeaderFile(root.file("config/spotless/LICENSE_HEADER.txt"))
            .updateYearWithLatest(true)
    }
}

fun SpotlessExtension.configGoogleLicenseHeader(project: Project) {
    val root = project.rootProject

    format("googleLicense", KotlinExtension::class.java) {
        target(*googleLicences.toTypedArray())
        licenseHeaderFile(root.file("config/spotless/LICENSE_HEADER_GOOGLE.txt"))
            .updateYearWithLatest(true)
    }
}

fun SpotlessExtension.configSquareLicenseHeader(project: Project, targets: Collection<Any>) {
    val root = project.rootProject

    format("squareLicense", KotlinExtension::class.java) {
        target(*targets.toTypedArray())
        licenseHeaderFile(root.file("config/spotless/LICENSE_HEADER_SQUARE.txt"))
            .updateYearWithLatest(true)
    }
}
