package love.forte.codegentle.common

/**
 * An extension interface used for commonizing builder logic.
 * For internal use only, external compatibility is not guaranteed and may change in the future.
 */
@RequiresOptIn(
    message = "This is an internal extension API for builder logic. " +
        "For internal use only, external compatibility is not guaranteed and may change in future versions.",
    level = RequiresOptIn.Level.ERROR
)
public annotation class CodeGentleBuilderExtensionImplementation

