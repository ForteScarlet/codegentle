/*
 * Copyright (C) 2025-2026 Forte Scarlet
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

import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.spec.internal.JavaRecordTypeSpecBuilderImpl

/**
 * A generated `record` type.
 *
 * ```java
 * public record RecordClass(int value) {
 * }
 * ```
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaRecordTypeSpec : NamedSpec, JavaTypeSpec {
    override val kind: JavaTypeSpec.Kind
        get() = JavaTypeSpec.Kind.RECORD

    override val name: String

    override val superclass: TypeName?
        get() = null

    public val mainConstructorParameters: List<JavaParameterSpec>

    /*
     * Init constructor:
     *
     * ```java
     * public record Student(String name, int age) {
     *   // initializerBlock
     *   public Student {
     *     require(age > 0, "...")
     *   }
     *
     *   static {  }
     *
     *   // Other constructor
     *   public Student(String name) {
     *     this(name, 24)
     *   }
     * }
     * ```
     */

    public companion object {
        /**
         * Create a builder for a record type.
         *
         * @param name the record type name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return JavaRecordTypeSpecBuilderImpl(name)
        }
    }

    /**
     * Builder for [JavaRecordTypeSpec].
     */
    public interface Builder :
        JavaTypeSpecBuilder<JavaRecordTypeSpec, Builder>,
        SuperinterfaceCollector<Builder> {

        override val kind: JavaTypeSpec.Kind
            get() = JavaTypeSpec.Kind.RECORD

        /**
         * Add main constructor parameter to this record type.
         */
        public fun addMainConstructorParameter(mainConstructorParameter: JavaParameterSpec): Builder

        /**
         * Add main constructor parameters to this record type.
         */
        public fun addMainConstructorParameters(vararg mainConstructorParams: JavaParameterSpec): Builder

        /**
         * Add main constructor parameters to this record type.
         */
        public fun addMainConstructorParameters(mainConstructorParams: Iterable<JavaParameterSpec>): Builder
    }
}

/**
 * Create a [JavaRecordTypeSpec] with the given name.
 *
 * @param name the record type name
 * @param block the configuration block
 * @return a new [JavaRecordTypeSpec] instance
 */
public inline fun JavaRecordTypeSpec(
    name: String,
    block: JavaRecordTypeSpec.Builder.() -> Unit = {},
): JavaRecordTypeSpec {
    return JavaRecordTypeSpec.builder(name).apply(block).build()
}

/**
 * Add main constructor parameter to this record type builder.
 *
 * @param type the parameter type
 * @param name the parameter name
 * @param block the parameter configuration block
 * @return this builder
 */
public inline fun JavaRecordTypeSpec.Builder.addMainConstructorParameter(
    type: TypeRef<*>,
    name: String,
    block: JavaParameterSpec.Builder.() -> Unit = {}
): JavaRecordTypeSpec.Builder {
    return addMainConstructorParameter(
        JavaParameterSpec(name, type, block)
    )
}
