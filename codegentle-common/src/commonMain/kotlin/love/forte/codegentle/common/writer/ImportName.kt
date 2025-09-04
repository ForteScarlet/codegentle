package love.forte.codegentle.common.writer

import love.forte.codegentle.common.naming.*

/**
 *
 * @author ForteScarlet
 */
@InternalWriterApi
public sealed class ImportName {
    public abstract val name: String
    public abstract val type: TypeName
    public abstract val packageName: PackageName
    public abstract val canonicalName: String

    @InternalWriterApi
    public data class Class(override val type: ClassName) : ImportName() {
        override val name: String get() = type.simpleName
        override val packageName: PackageName get() = type.packageName
        override val canonicalName: String get() = type.canonicalName
    }

    @InternalWriterApi
    public data class Member(override val type: MemberName) : ImportName() {
        override val name: String get() = type.name
        override val packageName: PackageName get() = type.packageName
        override val canonicalName: String get() = type.canonicalName
    }
}
