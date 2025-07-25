package love.forte.codegentle.common.ref

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl

/**
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface AnnotationRefBuildable<B : AnnotationRefBuildable<B>> : BuilderDsl {
    /**
     * Adds a member code value as a multiple member, allowing multiple values for the same member name.
     * This function always creates or appends to a multiple-value member, regardless of existing values.
     *
     * @param name The name of the member to add
     * @param codeValues The code values to add as member values
     * @return This builder instance
     */
    public fun addMultipleMembers(name: String, codeValues: Iterable<CodeValue>): B

    /**
     * Adds a member code value as a multiple member, allowing multiple values for the same member name.
     * This function always creates or appends to a multiple-value member, regardless of existing values.
     *
     * @param name The name of the member to add
     * @param codeValues The code values to add as member values
     * @return This builder instance
     */
    public fun addMultipleMembers(name: String, vararg codeValues: CodeValue): B =
        addMultipleMembers(name, codeValues.asList())

    /**
     * Adds a member code value with single or multiple behavior.
     *
     * The behavior depends on whether the member name already exists:
     * - If [name] does not exist: Creates a new [single member][AnnotationRef.MemberValue.Single]
     * - If [name] exists: Transforms existing single member to [multiple member][AnnotationRef.MemberValue.Multiple]
     *   and adds this value
     *
     * @param name The name of the member to add
     * @param codeValue The code value to add as a member value
     * @return This builder instance
     */
    public fun addMember(name: String, codeValue: CodeValue): B

    /**
     * Convenience overload of [addMember] that creates a [CodeValue] from format string and arguments.
     *
     * The behavior depends on whether the member name already exists:
     * - If [name] does not exist: Creates a new [single member][AnnotationRef.MemberValue.Single]
     * - If [name] exists: Transforms existing single member to [multiple member][AnnotationRef.MemberValue.Multiple]
     *   and adds this value
     *
     * @param name The name of the member to add
     * @param format The format string for code value
     * @param argumentParts The argument parts for the format string
     * @return This builder instance
     */
    public fun addMember(name: String, format: String, vararg argumentParts: CodeArgumentPart): B =
        addMember(name, CodeValue(format, *argumentParts))

}

public inline fun <B : AnnotationRefBuildable<B>> B.addOneMultipleMember(
    name: String,
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): B = addMultipleMembers(name, listOf(CodeValue(format, block)))

public inline fun <B : AnnotationRefBuildable<B>> B.addMember(
    name: String,
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): B = addMember(name, CodeValue(format, block))
