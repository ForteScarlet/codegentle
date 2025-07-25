@file:OptIn(AnnotationTargetExtensionScope::class)

package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageNames
import love.forte.codegentle.common.ref.AnnotationRefBuilder
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.addAnnotationRef

/**
 * @see PackageNames
 * @see KotlinAnnotationNames
 */
public object KotlinAnnotationNames {

    // kotlin.annotation.*
    /**
     * The [Target] annotation in Kotlin
     */
    public val TARGET: ClassName = ClassName(PackageNames.KOTLIN_ANNOTATION, "Target")

    /**
     * The [Retention] annotation in Kotlin
     */
    public val RETENTION: ClassName = ClassName(PackageNames.KOTLIN_ANNOTATION, "Retention")

    /**
     * The [Repeatable] annotation in Kotlin
     */
    public val REPEATABLE: ClassName = ClassName(PackageNames.KOTLIN_ANNOTATION, "Repeatable")

    /**
     * The [kotlin.annotation.MustBeDocumented] annotation in Kotlin
     */
    public val MUST_BE_DOCUMENTED: ClassName = ClassName(PackageNames.KOTLIN_ANNOTATION, "MustBeDocumented")

    // kotlin.jvm.*
    /**
     * The [kotlin.jvm.JvmStatic] annotation in Kotlin
     */
    public val JVM_STATIC: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmStatic")

    /**
     * The [kotlin.jvm.JvmField] annotation in Kotlin
     */
    public val JVM_FIELD: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmField")

    /**
     * The [kotlin.jvm.JvmName] annotation in Kotlin
     */
    public val JVM_NAME: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmName")

    /**
     * The [kotlin.jvm.JvmOverloads] annotation in Kotlin
     */
    public val JVM_OVERLOADS: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmOverloads")

    /**
     * The [kotlin.jvm.JvmInline] annotation in Kotlin
     */
    public val JVM_INLINE: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmInline")

    /**
     * The [kotlin.jvm.JvmMultifileClass] annotation in Kotlin
     */
    public val JVM_MULTIFILE_CLASS: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmMultifileClass")

    /**
     * The [kotlin.jvm.JvmSuppressWildcards] annotation in Kotlin
     */
    public val JVM_SUPPRESS_WILDCARDS: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmSuppressWildcards")

    /**
     * The [kotlin.jvm.JvmWildcard] annotation in Kotlin
     */
    public val JVM_WILDCARD: ClassName = ClassName(PackageNames.KOTLIN_JVM, "JvmWildcard")

    /**
     * The [kotlin.Throws] annotation in Kotlin
     */
    public val THROWS: ClassName = ClassName(PackageNames.KOTLIN, "Throws")

    /**
     * The [kotlin.jvm.Transient] annotation in Kotlin
     */
    public val TRANSIENT: ClassName = ClassName(PackageNames.KOTLIN_JVM, "Transient")

    /**
     * The [kotlin.jvm.Volatile] annotation in Kotlin
     */
    public val VOLATILE: ClassName = ClassName(PackageNames.KOTLIN_JVM, "Volatile")

    /**
     * The [kotlin.jvm.Synchronized] annotation in Kotlin
     */
    public val SYNCHRONIZED: ClassName = ClassName(PackageNames.KOTLIN_JVM, "Synchronized")

    /**
     * The [kotlin.jvm.Strictfp] annotation in Kotlin
     */
    public val STRICTFP: ClassName = ClassName(PackageNames.KOTLIN_JVM, "Strictfp")

    // kotlin.js.*
    /**
     * The [kotlin.js.JsName] annotation in Kotlin
     */
    public val JS_NAME: ClassName = ClassName(PackageNames.KOTLIN_JS, "JsName")

    /**
     * The `JsModule` annotation in Kotlin
     */
    public val JS_MODULE: ClassName = ClassName(PackageNames.KOTLIN_JS, "JsModule")

    /**
     * The `JsNonModule` annotation in Kotlin
     */
    public val JS_NON_MODULE: ClassName = ClassName(PackageNames.KOTLIN_JS, "JsNonModule")

    /**
     * The [kotlin.js.JsExport] annotation in Kotlin
     */
    public val JS_EXPORT: ClassName = ClassName(PackageNames.KOTLIN_JS, "JsExport")

    // kotlin.*
    /**
     * The [Deprecated] annotation in Kotlin
     */
    public val DEPRECATED: ClassName = ClassName(PackageNames.KOTLIN, "Deprecated")

    /**
     * The ReplaceWith annotation in Kotlin
     */
    public val REPLACE_WITH: ClassName = ClassName(PackageNames.KOTLIN, "ReplaceWith")

    /**
     * The Suppress annotation in Kotlin
     */
    public val SUPPRESS: ClassName = ClassName(PackageNames.KOTLIN, "Suppress")

    /**
     * The [OptIn] annotation in Kotlin
     */
    public val OPT_IN: ClassName = ClassName(PackageNames.KOTLIN, "OptIn")

    /**
     * The [RequiresOptIn] annotation in Kotlin
     */
    public val REQUIRES_OPT_IN: ClassName = ClassName(PackageNames.KOTLIN, "RequiresOptIn")

    /**
     * The [DslMarker] annotation in Kotlin
     */
    public val DSL_MARKER: ClassName = ClassName(PackageNames.KOTLIN, "DslMarker")

    /**
     * The [UnsafeVariance] annotation in Kotlin
     */
    public val UNSAFE_VARIANCE: ClassName = ClassName(PackageNames.KOTLIN, "UnsafeVariance")

    /**
     * The [PublishedApi] annotation in Kotlin
     */
    public val PUBLISHED_API: ClassName = ClassName(PackageNames.KOTLIN, "PublishedApi")

    /**
     * The [BuilderInference] annotation in Kotlin
     */
    public val BUILDER_INFERENCE: ClassName = ClassName(PackageNames.KOTLIN, "BuilderInference")

    /**
     * The [OverloadResolutionByLambdaReturnType] annotation in Kotlin
     */
    public val OVERLOAD_RESOLUTION_BY_LAMBDA_RETURN_TYPE: ClassName =
        ClassName(PackageNames.KOTLIN, "OverloadResolutionByLambdaReturnType")
}


/**
 * Add [KotlinAnnotationNames.TARGET] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addTarget(
    block: context(KotlinTargetExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.TARGET) {
    context(KotlinTargetExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.RETENTION] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addRetention(
    block: context(KotlinRetentionExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.RETENTION) {
    context(KotlinRetentionExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.REPEATABLE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addRepeatable(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.REPEATABLE, block)

/**
 * Add [KotlinAnnotationNames.MUST_BE_DOCUMENTED] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addMustBeDocumented(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.MUST_BE_DOCUMENTED, block)

/**
 * Add [KotlinAnnotationNames.JVM_STATIC] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmStatic(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_STATIC, block)

/**
 * Add [KotlinAnnotationNames.JVM_FIELD] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmField(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_FIELD, block)

/**
 * Add [KotlinAnnotationNames.JVM_NAME] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmName(
    block: context(KotlinJvmNameExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_NAME) {
    context(KotlinJvmNameExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.JVM_OVERLOADS] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmOverloads(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_OVERLOADS, block)

/**
 * Add [KotlinAnnotationNames.JVM_INLINE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmInline(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_INLINE, block)

/**
 * Add [KotlinAnnotationNames.JVM_MULTIFILE_CLASS] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmMultifileClass(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_MULTIFILE_CLASS, block)

/**
 * Add [KotlinAnnotationNames.JVM_SUPPRESS_WILDCARDS] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmSuppressWildcards(
    block: context(KotlinJvmSuppressWildcardsExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_SUPPRESS_WILDCARDS) {
    context(KotlinJvmSuppressWildcardsExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.JVM_WILDCARD] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJvmWildcard(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JVM_WILDCARD, block)

/**
 * Add [KotlinAnnotationNames.THROWS] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addThrows(
    block: context(KotlinThrowsExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.THROWS) {
    context(KotlinThrowsExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.TRANSIENT] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addTransient(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.TRANSIENT, block)

/**
 * Add [KotlinAnnotationNames.VOLATILE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addVolatile(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.VOLATILE, block)

/**
 * Add [KotlinAnnotationNames.SYNCHRONIZED] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addSynchronized(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.SYNCHRONIZED, block)

/**
 * Add [KotlinAnnotationNames.STRICTFP] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addStrictfp(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.STRICTFP, block)

/**
 * Add [KotlinAnnotationNames.JS_NAME] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJsName(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JS_NAME, block)

/**
 * Add [KotlinAnnotationNames.JS_MODULE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJsModule(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JS_MODULE, block)

/**
 * Add [KotlinAnnotationNames.JS_NON_MODULE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJsNonModule(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JS_NON_MODULE, block)

/**
 * Add [KotlinAnnotationNames.JS_EXPORT] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addJsExport(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.JS_EXPORT, block)

/**
 * Add [KotlinAnnotationNames.DEPRECATED] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addDeprecated(
    block: context(KotlinDeprecatedExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.DEPRECATED) {
    context(KotlinDeprecatedExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.REPLACE_WITH] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addReplaceWith(
    block: context(KotlinReplaceWithExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.REPLACE_WITH) {
    context(KotlinReplaceWithExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.SUPPRESS] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addSuppress(
    block: context(KotlinSuppressExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.SUPPRESS) {
    context(KotlinSuppressExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.OPT_IN] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addOptIn(
    block: context(KotlinOptInExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.OPT_IN) {
    context(KotlinOptInExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.REQUIRES_OPT_IN] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addRequiresOptIn(
    block: context(KotlinRequiresOptInExtensionScope) AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.REQUIRES_OPT_IN) {
    context(KotlinRequiresOptInExtensionScope) {
        block()
    }
}

/**
 * Add [KotlinAnnotationNames.DSL_MARKER] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addDslMarker(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.DSL_MARKER, block)

/**
 * Add [KotlinAnnotationNames.UNSAFE_VARIANCE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addUnsafeVariance(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.UNSAFE_VARIANCE, block)

/**
 * Add [KotlinAnnotationNames.PUBLISHED_API] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addPublishedApi(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.PUBLISHED_API, block)

/**
 * Add [KotlinAnnotationNames.BUILDER_INFERENCE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addBuilderInference(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.BUILDER_INFERENCE, block)

/**
 * Add [KotlinAnnotationNames.OVERLOAD_RESOLUTION_BY_LAMBDA_RETURN_TYPE] into [B].
 */
public inline fun <B : AnnotationRefCollector<B>> B.addOverloadResolutionByLambdaReturnType(
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(KotlinAnnotationNames.OVERLOAD_RESOLUTION_BY_LAMBDA_RETURN_TYPE, block)

