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
package love.forte.codegentle.java.ref

import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.AnnotationRefCollectorOps
import love.forte.codegentle.common.ref.addAnnotation
import love.forte.codegentle.common.ref.addMember
import love.forte.codegentle.java.naming.JavaAnnotationNames


/**
 * Add annotation ref: [JavaAnnotationNames.Override]
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addOverride(): B {
    return collector.addAnnotation(JavaAnnotationNames.Override)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Deprecated]
 *
 * @param since `since` of `java.lang.Deprecated` since Java 9.
 * @param forRemoval `forRemoval` of `java.lang.Deprecated` since Java 9.
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addDeprecated(
    since: String? = null,
    forRemoval: Boolean? = null,
): B {
    return collector.addAnnotation(JavaAnnotationNames.Deprecated) {
        since?.also { since ->
            addMember("since", "%V") {
                emitString(since)
            }
        }
        forRemoval?.also { forRemoval ->
            addMember("forRemoval", forRemoval.toString())
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.SuppressWarnings]
 *
 * @param values `value` of `java.lang.SuppressWarnings`.
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addSuppressWarnings(vararg values: String): B {
    return collector.addAnnotation(JavaAnnotationNames.SuppressWarnings) {
        if (values.isNotEmpty()) {
            for (value in values) {
                addMember("value", "%V") { emitString(value) }
            }
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.SafeVarargs]
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addSafeVarargs(): B {
    return collector.addAnnotation(JavaAnnotationNames.SafeVarargs)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Documented]
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addDocumented(): B {
    return collector.addAnnotation(JavaAnnotationNames.Documented)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Retention]
 *
 * @param value `value` of `java.lang.annotation.Retention`
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addRetention(value: AnnotationRetention? = null): B {
    return collector.addAnnotation(JavaAnnotationNames.Retention) {
        val javaRetentionName = when (value) {
            AnnotationRetention.SOURCE -> "SOURCE"
            AnnotationRetention.BINARY -> "CLASS"
            AnnotationRetention.RUNTIME -> "RUNTIME"
            null -> null
        }

        javaRetentionName?.also { retentionName ->
            addMember("value", "%V.%V") {
                emitType(ClassName("java.lang.annotation", "Retention"))
                emitLiteral(retentionName)
            }
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.Target].
 *
 * Note that [values] are not validated.
 *
 * @param values Elements of `java.lang.annotation.ElementType`.
 * All chars must meet `it in 'A'..'Z' || it == '_'`.
 *
 * @throws IllegalArgumentException if any chars not meet `it in 'A'..'Z' || it == '_'`
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addTarget(vararg values: String): B {
    return collector.addAnnotation(JavaAnnotationNames.Target) {
        if (values.isNotEmpty()) {
            for (value in values) {
                val elementName = value.uppercase()
                require(elementName.all { it in 'A'..'Z' || it == '_' }) { "Invalid element name: $value" }
                addMember("value", "%V.%V") {
                    emitType(ClassName("java.lang.annotation", "ElementType"))
                    emitLiteral(elementName)
                }
            }
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.Inherited].
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addInherited(): B {
    return collector.addAnnotation(JavaAnnotationNames.Inherited)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Repeatable].
 *
 * @param value `value` of `java.lang.annotation.Repeatable`
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addRepeatable(value: ClassName): B {
    return collector.addAnnotation(JavaAnnotationNames.Repeatable) {
        addMember("value", "%V.class") {
            emitType(value)
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.Native].
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addNative(): B {
    return collector.addAnnotation(JavaAnnotationNames.Native)
}

/**
 * Add annotation ref: [JavaAnnotationNames.FunctionalInterface].
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addFunctionalInterface(): B {
    return collector.addAnnotation(JavaAnnotationNames.FunctionalInterface)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Generated] since Java 9.
 *
 * @param values `value` of `javax.annotation.processing.Generated`.
 * @param date `date` of `javax.annotation.processing.Generated`.
 * @param comments `comments` of `javax.annotation.processing.Generated`.
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addGenerated(
    values: Array<String>? = null,
    date: String? = null,
    comments: String? = null
): B {
    return collector.addAnnotation(JavaAnnotationNames.Generated) {
        values?.takeIf { it.isNotEmpty() }?.also { values ->
            for (value in values) {
                addMember("value", "%V") { emitString(value) }
            }
        }
        date?.also { d -> addMember("date", "%V") { emitString(d) } }
        comments?.also { c -> addMember("comments", "%V") { emitString(c) } }
    }
}
