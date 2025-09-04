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
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface JavaMethodCollector<B : JavaMethodCollector<B>> {
    /**
     * Add methods to this annotation type.
     */
    public fun addMethods(methods: Iterable<JavaMethodSpec>): B

    /**
     * Add methods to this annotation type.
     */
    public fun addMethods(vararg methods: JavaMethodSpec): B =
        addMethods(methods.asList())

    /**
     * Add a method to this annotation type.
     */
    public fun addMethod(method: JavaMethodSpec): B
}

/**
 * Add a method with the given name and void return type.
 *
 * @param name the method name
 * @param block the configuration block for the method
 * @return this collector instance
 */
public inline fun <B : JavaMethodCollector<B>> B.addMethod(
    name: String,
    block: JavaMethodSpec.Builder.() -> Unit = {}
): B = addMethod(JavaMethodSpec(name, block))

/**
 * Add a method with the given name and return type.
 *
 * @param name the method name
 * @param returnType the return type
 * @param block the configuration block for the method
 * @return this collector instance
 */
public inline fun <B : JavaMethodCollector<B>> B.addMethod(
    name: String,
    returnType: TypeRef<*>,
    block: JavaMethodSpec.Builder.() -> Unit = {}
): B = addMethod(JavaMethodSpec(name) { returns(returnType); block() })

/**
 * Add a method with the given name and return type.
 *
 * @param name the method name
 * @param returnType the return type
 * @param block the configuration block for the method
 * @return this collector instance
 */
public inline fun <B : JavaMethodCollector<B>> B.addMethod(
    name: String,
    returnType: TypeName,
    block: JavaMethodSpec.Builder.() -> Unit = {}
): B = addMethod(name, returnType.ref(), block)

/**
 * Add a main method to this type.
 *
 * @param block the configuration block for the main method
 * @return this collector instance
 */
public inline fun <B : JavaMethodCollector<B>> B.addMainMethod(
    block: JavaMethodSpec.Builder.() -> Unit = {}
): B = addMethod(JavaMethodSpec.mainBuilder().apply(block).build())
