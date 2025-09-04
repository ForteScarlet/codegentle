/*
 * Copyright (C) 2014-2024 Square, Inc.
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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.spec.internal.JavaAnnotationTypeSpecBuilderImpl

/**
 * A generated annotation type.
 * ```java
 * public @interface Anno {
 * }
 * ```
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaAnnotationTypeSpec : NamedSpec, JavaTypeSpec {
    override val name: String

    override val superclass: TypeName?
        get() = null

    override val superinterfaces: List<TypeName>
        get() = emptyList()

    public companion object {
        /**
         * Create a builder for an annotation type.
         *
         * @param name the annotation type name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return JavaAnnotationTypeSpecBuilderImpl(name)
        }
    }

    /**
     * Builder for [JavaAnnotationTypeSpec].
     */
    public interface Builder : JavaTypeSpecBuilder<JavaAnnotationTypeSpec, Builder> {
        override val kind: JavaTypeSpec.Kind
            get() = JavaTypeSpec.Kind.ANNOTATION
    }
}

/**
 * Create a [JavaAnnotationTypeSpec] with the given name.
 *
 * @param name the annotation type name
 * @param block the configuration block
 * @return a new [JavaAnnotationTypeSpec] instance
 */
public inline fun JavaAnnotationTypeSpec(
    name: String,
    block: JavaAnnotationTypeSpec.Builder.() -> Unit = {}
): JavaAnnotationTypeSpec {
    return JavaAnnotationTypeSpec.builder(name).apply(block).build()
}
