/*
 * Copyright (C) 2025 Forte Scarlet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.forte.codegentle.java.naming

import love.forte.codegentle.common.naming.ClassName

public object JavaAnnotationNames {
    /**
     * see `java.lang.Override`
     */
    public val Override: ClassName = ClassName("java.lang", "Override")

    /**
     * see `java.lang.Deprecated`
     */
    public val Deprecated: ClassName = ClassName("java.lang", "Deprecated")

    /**
     * see `java.lang.SuppressWarnings`
     */
    public val SuppressWarnings: ClassName = ClassName("java.lang", "SuppressWarnings")

    /**
     * see `java.lang.SafeVarargs`
     */
    public val SafeVarargs: ClassName = ClassName("java.lang", "SafeVarargs")

    /**
     * see `java.lang.annotation.Documented`
     */
    public val Documented: ClassName = ClassName("java.lang", "Documented")

    /**
     * see `java.lang.annotation.Retention`
     */
    public val Retention: ClassName = ClassName("java.lang.annotation", "Retention")

    /**
     * see `java.lang.annotation.Target`
     */
    public val Target: ClassName = ClassName("java.lang.annotation", "Target")

    /**
     * see `java.lang.annotation.Inherited`
     */
    public val Inherited: ClassName = ClassName("java.lang.annotation", "Inherited")

    /**
     * see `java.lang.annotation.Repeatable`
     */
    public val Repeatable: ClassName = ClassName("java.lang.annotation", "Repeatable")

    /**
     * see `java.lang.annotation.Native`
     *
     * since Java 1.8
     */
    public val Native: ClassName = ClassName("java.lang.annotation", "Native")

    /**
     * see `java.lang.FunctionalInterface`
     *
     * since Java 1.8
     */
    public val FunctionalInterface: ClassName = ClassName("java.lang", "FunctionalInterface")

    /**
     * see `javax.annotation.processing.Generated` in module `java.compiler` .
     *
     * since Java 9
     */
    public val Generated: ClassName = ClassName("javax.annotation.processing", "Generated")

}
