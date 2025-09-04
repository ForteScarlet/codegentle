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

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.code.CodeValue

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface JavaSubtypeCollector<B : JavaSubtypeCollector<B>> {

    /**
     * Add nested types to this record type.
     */
    public fun addSubtypes(vararg types: JavaTypeSpec): B =
        addSubtypes(types.asList())

    /**
     * Add nested types to this record type.
     */
    public fun addSubtypes(types: Iterable<JavaTypeSpec>): B

    /**
     * Add a nested type to this record type.
     */
    public fun addSubtype(type: JavaTypeSpec): B
}

/**
 * Adds a sub annotation type to this [JavaSubtypeCollector].
 *
 * @param name the name of the annotation type to add
 * @param block a lambda for configuring the [JavaAnnotationTypeSpec.Builder]
 * @return this [JavaSubtypeCollector] itself
 */
public inline fun <C : JavaSubtypeCollector<B>, B> C.addSubAnnotationType(
    name: String,
    block: JavaAnnotationTypeSpec.Builder.() -> Unit
): B {
    return addSubtype(JavaAnnotationTypeSpec(name, block))
}

/**
 * Adds a sub enum type to this [JavaSubtypeCollector].
 *
 * @param name the name of the enum type to add
 * @param block a lambda for configuring the [JavaEnumTypeSpec.Builder]
 * @return this [JavaSubtypeCollector] itself
 */
public inline fun <C : JavaSubtypeCollector<B>, B> C.addSubEnumType(
    name: String,
    block: JavaEnumTypeSpec.Builder.() -> Unit = {}
): B {
    return addSubtype(JavaEnumTypeSpec(name, block))
}

/**
 * Adds a sub record type to this [JavaSubtypeCollector].
 *
 * @param name the name of the record type to add
 * @param block a lambda for configuring the [JavaRecordTypeSpec.Builder]
 * @return this [JavaSubtypeCollector] itself
 */
public inline fun <C : JavaSubtypeCollector<B>, B> C.addSubRecordType(
    name: String,
    block: JavaRecordTypeSpec.Builder.() -> Unit = {}
): B {
    return addSubtype(JavaRecordTypeSpec(name, block))
}

/**
 * Adds a sub sealed type to this [JavaSubtypeCollector].
 *
 * @param kind the type kind (SEALED_CLASS or SEALED_INTERFACE)
 * @param name the name of the sealed type to add
 * @param block a lambda for configuring the [JavaSealedTypeSpec.Builder]
 * @return this [JavaSubtypeCollector] itself
 */
public inline fun <C : JavaSubtypeCollector<B>, B> C.addSubSealedType(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): B {
    return addSubtype(JavaSealedTypeSpec(kind, name, block))
}

/**
 * Adds a sub simple type to this [JavaSubtypeCollector].
 *
 * @param kind the type kind (CLASS or INTERFACE)
 * @param name the name of the simple type to add
 * @param block a lambda for configuring the [JavaSimpleTypeSpec.Builder]
 * @return this [JavaSubtypeCollector] itself
 */
public inline fun <C : JavaSubtypeCollector<B>, B> C.addSubSimpleType(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): B {
    return addSubtype(JavaSimpleTypeSpec(kind, name, block))
}

/**
 * Adds a sub anonymous class type to this [JavaSubtypeCollector].
 *
 * @param anonymousTypeArguments the anonymous type arguments
 * @param block a lambda for configuring the [JavaAnonymousClassTypeSpec.Builder]
 * @return this [JavaSubtypeCollector] itself
 */
public inline fun <C : JavaSubtypeCollector<B>, B> C.addSubAnonymousClassType(
    anonymousTypeArguments: CodeValue,
    block: JavaAnonymousClassTypeSpec.Builder.() -> Unit = {}
): B {
    return addSubtype(JavaAnonymousClassTypeSpec(anonymousTypeArguments, block))
}

/**
 * Adds a sub non-sealed type to this [JavaSubtypeCollector].
 *
 * @param kind the type kind (NON_SEALED_CLASS or NON_SEALED_INTERFACE)
 * @param name the name of the non-sealed type to add
 * @param block a lambda for configuring the [JavaNonSealedTypeSpec.Builder]
 * @return this [JavaSubtypeCollector] itself
 */
public inline fun <C : JavaSubtypeCollector<B>, B> C.addSubNonSealedType(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): B {
    return addSubtype(JavaNonSealedTypeSpec(kind, name, block))
}
