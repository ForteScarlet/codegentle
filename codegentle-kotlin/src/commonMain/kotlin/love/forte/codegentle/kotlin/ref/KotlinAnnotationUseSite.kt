package love.forte.codegentle.kotlin.ref

/**
 *
 * @author ForteScarlet
 */
public enum class KotlinAnnotationUseSite {
    /** Used for annotations on a file */
    FILE,

    /** Used for annotations on a field */
    FIELD,

    /**
     * Used for annotations on a property.
     * Note: Annotations with this target are not visible to Java
     */
    PROPERTY,

    /** Used for annotations on a property getter */
    GET,

    /** Used for annotations on a property setter */
    SET,

    /**
     * An experimental meta-target for properties.
     */
    ALL,

    /** Used for annotations on a receiver parameter of an extension function or property */
    RECEIVER,

    /** constructor parameter */
    PARAM,

    /** Used for annotations on a property setter parameter */
    SETPARAM,

    /** Used for annotations on the field storing the delegate instance for a delegated property */
    DELEGATE
}

