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
package love.forte.codegentle.kotlin.ref

/**
 *
 * @author ForteScarlet
 */
public enum class KotlinAnnotationUseSite {
    /** Used for annotations on a file */
    FILE,

    /** Used for annotations on a field */
    FIELD,

    /**
     * Used for annotations on a property.
     * Note: Annotations with this target are not visible to Java
     */
    PROPERTY,

    /** Used for annotations on a property getter */
    GET,

    /** Used for annotations on a property setter */
    SET,

    /**
     * An experimental meta-target for properties.
     */
    ALL,

    /** Used for annotations on a receiver parameter of an extension function or property */
    RECEIVER,

    /** constructor parameter */
    PARAM,

    /** Used for annotations on a property setter parameter */
    SETPARAM,

    /** Used for annotations on the field storing the delegate instance for a delegated property */
    DELEGATE
}

