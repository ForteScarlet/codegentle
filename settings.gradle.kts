/*
 *     Copyright (c) 2021-2025. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "codegentle"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":codegentle-common")
include(":codegentle-common-ksp")
include(":codegentle-java")
include(":codegentle-kotlin")
include(":codegentle-kotlin-ksp")

// Execution failed for task ':tests:test-java-apt:proj:compileJava'.
//  > java.lang.NoClassDefFoundError: love/forte/codegentle/common/naming/ClassName_jvmKt
// include("tests:test-java-apt:proc")
// include("tests:test-java-apt:proj")

include("tests:test-ksp-receiver-and-contexts:proc")
include("tests:test-ksp-receiver-and-contexts:proj")

include("internal:code-value-extensions")
include("internal:enum-set")
include("internal:enum-set-test")

include(":tests:simple")

include(":showcase:showcase-web")
