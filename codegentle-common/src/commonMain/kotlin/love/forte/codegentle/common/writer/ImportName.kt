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
