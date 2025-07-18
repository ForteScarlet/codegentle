package love.forte.codegentle.java.ref

import love.forte.codegentle.common.code.CodePart.Companion.literal
import love.forte.codegentle.common.code.CodePart.Companion.string
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.AnnotationRefCollectorOps
import love.forte.codegentle.common.ref.addAnnotationRef
import love.forte.codegentle.common.ref.addMember
import love.forte.codegentle.java.naming.JavaAnnotationNames


/**
 * Add annotation ref: [JavaAnnotationNames.Override]
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addOverride(): B {
    return collector.addAnnotationRef(JavaAnnotationNames.Override)
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
    return collector.addAnnotationRef(JavaAnnotationNames.Deprecated) {
        since?.also { since ->
            addMember("since", "%V") {
                string(since)
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
    return collector.addAnnotationRef(JavaAnnotationNames.SuppressWarnings) {
        if (values.isNotEmpty()) {
            for (value in values) {
                addMember("value", "%V") { string(value) }
            }
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.SafeVarargs]
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addSafeVarargs(): B {
    return collector.addAnnotationRef(JavaAnnotationNames.SafeVarargs)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Documented]
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addDocumented(): B {
    return collector.addAnnotationRef(JavaAnnotationNames.Documented)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Retention]
 *
 * @param value `value` of `java.lang.annotation.Retention`
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addRetention(value: AnnotationRetention? = null): B {
    return collector.addAnnotationRef(JavaAnnotationNames.Retention) {
        val javaRetentionName = when (value) {
            AnnotationRetention.SOURCE -> "SOURCE"
            AnnotationRetention.BINARY -> "CLASS"
            AnnotationRetention.RUNTIME -> "RUNTIME"
            null -> null
        }

        javaRetentionName?.also { retentionName ->
            addMember("value", "%V.%V") {
                emitType(ClassName("java.lang.annotation", "Retention"))
                literal(retentionName)
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
    return collector.addAnnotationRef(JavaAnnotationNames.Target) {
        if (values.isNotEmpty()) {
            for (value in values) {
                val elementName = value.uppercase()
                require(elementName.all { it in 'A'..'Z' || it == '_' })
                addMember("value", "%V.%V") {
                    emitType(ClassName("java.lang.annotation", "ElementType"))
                    literal(elementName)
                }
            }
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.Inherited].
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addInherited(): B {
    return collector.addAnnotationRef(JavaAnnotationNames.Inherited)
}

/**
 * Add annotation ref: [JavaAnnotationNames.Repeatable].
 *
 * @param value `value` of `java.lang.annotation.Repeatable`
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addRepeatable(value: ClassName): B {
    return collector.addAnnotationRef(JavaAnnotationNames.Repeatable) {
        addMember("value", "%V.class") {
            emitType(value)
        }
    }
}

/**
 * Add annotation ref: [JavaAnnotationNames.Native].
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addNative(): B {
    return collector.addAnnotationRef(JavaAnnotationNames.Native)
}

/**
 * Add annotation ref: [JavaAnnotationNames.FunctionalInterface].
 */
public fun <B : AnnotationRefCollector<B>> AnnotationRefCollectorOps<B>.addFunctionalInterface(): B {
    return collector.addAnnotationRef(JavaAnnotationNames.FunctionalInterface)
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
    return collector.addAnnotationRef(JavaAnnotationNames.Generated) {
        values?.takeIf { it.isNotEmpty() }?.also { values ->
            for (value in values) {
                addMember("value", "%V") { emitString(value) }
            }
        }
        date?.also { d -> addMember("date", "%V") { emitString(d) } }
        comments?.also { c -> addMember("comments", "%V") { emitString(c) } }
    }
}
