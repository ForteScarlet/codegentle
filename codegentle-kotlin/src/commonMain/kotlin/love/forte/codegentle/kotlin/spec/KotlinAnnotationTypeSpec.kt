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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.kotlin.spec.internal.KotlinAnnotationTypeSpecBuilderImpl

/**
 * A Kotlin annotation class.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinAnnotationTypeSpec : KotlinTypeSpec {
    /**
     * The name of this annotation class
     */
    override val name: String

    /**
     * The kind of this annotation type, always returns [KotlinTypeSpec.Kind.CLASS]
     */
    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    /**
     * Always returns null since the annotation class cannot have a superclass
     */
    override val superclass: TypeName?
        get() = null

    /**
     * Always returns an empty list since annotation class cannot have superinterfaces
     */
    override val superinterfaces: List<TypeName>
        get() = emptyList()

    /**
     * The properties of this annotation type. Must be immutable `val`.
     */
    override val properties: List<KotlinPropertySpec>

    public companion object {
        /**
         * Create a builder for an annotation class.
         *
         * @param name the annotation class name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return KotlinAnnotationTypeSpecBuilderImpl(name)
        }
    }

    /**
     * Builder for [KotlinAnnotationTypeSpec].
     */
    public interface Builder :
        KotlinTypeSpecBuilder<KotlinAnnotationTypeSpec, Builder>,
        KotlinPropertyCollector<Builder> {
        /**
         * The annotation class name.
         */
        public val name: String
    }
}

/**
 * Create a [KotlinAnnotationTypeSpec] with the given name.
 *
 * @param name the annotation class name
 * @param block the configuration block
 * @return a new [KotlinAnnotationTypeSpec] instance
 */
public inline fun KotlinAnnotationTypeSpec(
    name: String,
    block: KotlinAnnotationTypeSpec.Builder.() -> Unit = {}
): KotlinAnnotationTypeSpec {
    return KotlinAnnotationTypeSpec.builder(name).apply(block).build()
}
