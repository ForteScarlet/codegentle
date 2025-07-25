package love.forte.codegentle.common

/**
 * Mark an `enum class` to generate a `Set<TheEnum>` implementation
 */
@Retention(AnnotationRetention.SOURCE)
public annotation class GenEnumSet(
    val internal: Boolean = false,
    val mutableName: String = "",
    val immutableName: String = "",
    val containerName: String = "",
    val containerSingleAdder: String = "",
    val containerMultiAdder: String = "",
    val operatorsName: String = "",
) {

    /**
     * A set of mutually exclusive enum elements.
     * Within this group, setting one element will cancel the settings of other elements in the group.
     */
    public annotation class Group(val name: String)
}
