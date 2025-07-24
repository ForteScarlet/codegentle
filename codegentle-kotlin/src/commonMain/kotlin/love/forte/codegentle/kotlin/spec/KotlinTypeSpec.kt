package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.GenEnumSet
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.isNotEmpty
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierContainer

/**
 * A Kotlin type.
 */
public sealed interface KotlinTypeSpec : KotlinSpec, KotlinModifierContainer {
    /**
     * 获取类型的种类。
     *
     * @return 类型的种类
     */
    public val kind: Kind

    /**
     * 获取类型的名称。
     *
     * @return 类型的名称
     */
    public val name: String

    public val kDoc: CodeValue
    public val annotations: List<AnnotationRef>
    override val modifiers: Set<KotlinModifier>
    public val typeVariables: List<TypeRef<TypeVariableName>>

    // super class:
    //  `extends` One if is class,
    //  Nothing if is enum, annotation, interface.

    /**
     * Super class.
     *
     * @see love.forte.codegentle.kotlin.naming.KotlinDelegatedClassName
     */
    public val superclass: TypeName?

    /**
     * Super interfaces.
     *
     * @see love.forte.codegentle.kotlin.naming.KotlinDelegatedClassName
     */
    public val superinterfaces: List<TypeName>

    public val properties: List<KotlinPropertySpec>

    public val initializerBlock: CodeValue

    public val functions: List<KotlinFunctionSpec>

    public val subtypes: List<KotlinTypeSpec>

    public fun isMemberEmpty(): Boolean {
        if (properties.isNotEmpty()) {
            return false
        }

        if (functions.isNotEmpty()) {
            return false
        }

        if (subtypes.isNotEmpty()) {
            return false
        }

        if (initializerBlock.isNotEmpty()) {
            return false
        }

        return true
    }

    @GenEnumSet(
        internal = true,
        mutableName = "MutableKotlinTypeSpecKindSet",
        immutableName = "KotlinTypeSpecKindSet"
    )
    public enum class Kind(
        internal val keyword: String,
    ) {
        /**
         * 类
         */
        CLASS("class"),

        /**
         * 接口
         */
        INTERFACE("interface"),

        /**
         * 对象
         */
        OBJECT("object"),

        /**
         * 类型别名
         */
        TYPE_ALIAS("typealias")
    }

    public companion object {
        /**
         * Create a builder for a class.
         *
         * @param name the class name
         * @return a new builder
         */
        public fun classBuilder(name: String): KotlinSimpleTypeSpec.Builder {
            return KotlinSimpleTypeSpec.builder(Kind.CLASS, name)
        }

        /**
         * Create a builder for an interface.
         *
         * @param name the interface name
         * @return a new builder
         */
        public fun interfaceBuilder(name: String): KotlinSimpleTypeSpec.Builder {
            return KotlinSimpleTypeSpec.builder(Kind.INTERFACE, name)
        }

        /**
         * Create a builder for an object.
         *
         * @param name the object name
         * @return a new builder
         */
        public fun objectBuilder(name: String): KotlinObjectTypeSpec.Builder {
            return KotlinObjectTypeSpec.builder(name)
        }

        /**
         * Create a builder for a companion object.
         *
         * @return a new builder
         */
        public fun companionObjectBuilder(): KotlinObjectTypeSpec.Builder {
            return KotlinObjectTypeSpec.companionBuilder()
        }

        /**
         * Create a builder for an enum class.
         *
         * @param name the enum class name
         * @return a new builder
         */
        public fun enumBuilder(name: String): KotlinEnumTypeSpec.Builder {
            return KotlinEnumTypeSpec.builder(name)
        }

        /**
         * Create a builder for an annotation class.
         *
         * @param name the annotation class name
         * @return a new builder
         */
        public fun annotationBuilder(name: String): KotlinAnnotationTypeSpec.Builder {
            return KotlinAnnotationTypeSpec.builder(name)
        }

        /**
         * Create a builder for a value class.
         *
         * @param name the value class name
         * @param primaryParameter the primary constructor parameter
         * @return a new builder
         */
        public fun valueClassBuilder(
            name: String,
            primaryParameter: KotlinValueParameterSpec
        ): KotlinValueClassSpec.Builder {
            return KotlinValueClassSpec.builder(name, primaryParameter)
        }

        /**
         * Create a builder for a type alias.
         *
         * @param name the type alias name
         * @return a new builder
         */
        public fun typeAliasBuilder(name: String): KotlinSimpleTypeSpec.Builder {
            return KotlinSimpleTypeSpec.builder(Kind.TYPE_ALIAS, name)
        }
    }
}

// extensions

public val KotlinTypeSpec.isClass: Boolean
    get() = kind == KotlinTypeSpec.Kind.CLASS

public val KotlinTypeSpec.isAnnotationClass: Boolean
    get() = kind == KotlinTypeSpec.Kind.CLASS && KotlinModifier.ANNOTATION in modifiers

public val KotlinTypeSpec.isEnumClass: Boolean
    get() = kind == KotlinTypeSpec.Kind.CLASS && KotlinModifier.ENUM in modifiers

public val KotlinTypeSpec.isValueClass: Boolean
    get() = kind == KotlinTypeSpec.Kind.CLASS && KotlinModifier.VALUE in modifiers

public val KotlinTypeSpec.isSealedClass: Boolean
    get() = kind == KotlinTypeSpec.Kind.CLASS && KotlinModifier.SEALED in modifiers

public val KotlinTypeSpec.isInterface: Boolean
    get() = kind == KotlinTypeSpec.Kind.INTERFACE

public val KotlinTypeSpec.isSealedInterface: Boolean
    get() = kind == KotlinTypeSpec.Kind.INTERFACE && KotlinModifier.SEALED in modifiers

public val KotlinTypeSpec.isFunInterface: Boolean
    get() = kind == KotlinTypeSpec.Kind.INTERFACE && KotlinModifier.FUN in modifiers

public val KotlinTypeSpec.isObject: Boolean
    get() = kind == KotlinTypeSpec.Kind.OBJECT

public val KotlinTypeSpec.isCompanionObject: Boolean
    get() = kind == KotlinTypeSpec.Kind.OBJECT && KotlinModifier.COMPANION in modifiers

public fun KotlinTypeSpec.isMemberNotEmpty(): Boolean = !isMemberEmpty()
