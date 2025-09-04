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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.spec.internal.JavaEnumTypeSpecBuilderImpl

/**
 * A generated `enum` type.
 *
 * ```java
 * public enum EnumType {
 * }
 * ```
 *
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaEnumTypeSpec : NamedSpec, JavaTypeSpec {
    override val kind: JavaTypeSpec.Kind
        get() = JavaTypeSpec.Kind.ENUM

    override val name: String

    override val superclass: TypeName?
        get() = null

    public val enumConstants: Map<String, JavaAnonymousClassTypeSpec>

    public companion object {
        /**
         * Create a builder for an enum class.
         *
         * @param name the enum class name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return JavaEnumTypeSpecBuilderImpl(name)
        }
    }

    /**
     * Builder for [JavaEnumTypeSpec].
     */
    public interface Builder :
        JavaTypeSpecBuilder<JavaEnumTypeSpec, Builder>,
        SuperinterfaceCollector<Builder> {
        override val kind: JavaTypeSpec.Kind
            get() = JavaTypeSpec.Kind.ENUM

        /**
         * Add enum constant.
         */
        public fun addEnumConstant(name: String, type: JavaAnonymousClassTypeSpec): Builder

        /**
         * Build [JavaEnumTypeSpec] instance.
         */
        override fun build(): JavaEnumTypeSpec
    }
}

/**
 * Create a [JavaEnumTypeSpec] with the given name.
 *
 * @param name the enum class name
 * @param block the configuration block
 * @return a new [JavaEnumTypeSpec] instance
 */
public inline fun JavaEnumTypeSpec(
    name: String,
    block: JavaEnumTypeSpec.Builder.() -> Unit = {}
): JavaEnumTypeSpec {
    return JavaEnumTypeSpec.builder(name).apply(block).build()
}

/**
 * Add enum constant with anonymous type.
 */
public inline fun JavaEnumTypeSpec.Builder.addEnumConstant(
    name: String,
    anonymousTypeArguments: CodeValue,
    block: JavaAnonymousClassTypeSpec.Builder.() -> Unit = {}
): JavaEnumTypeSpec.Builder {
    return addEnumConstant(
        name,
        JavaAnonymousClassTypeSpec(anonymousTypeArguments, block)
    )
}
