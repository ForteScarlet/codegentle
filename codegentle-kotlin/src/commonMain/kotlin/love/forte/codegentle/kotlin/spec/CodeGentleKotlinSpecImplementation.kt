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

import kotlin.annotation.AnnotationTarget.*

/**
 * 标记一个类是 CodeGentle Kotlin 规范的实现。
 *
 * 这个注解用于标记那些实现了 CodeGentle Kotlin 规范接口的类。
 * 使用这个注解的类应该是内部实现，不应该被外部代码直接使用。
 */
@RequiresOptIn(
    message = "this is an internally implemented spec API",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
@Target(
    CLASS,
    FUNCTION,
    PROPERTY,
    CONSTRUCTOR,
    TYPEALIAS
)
@MustBeDocumented
public annotation class CodeGentleKotlinSpecImplementation
