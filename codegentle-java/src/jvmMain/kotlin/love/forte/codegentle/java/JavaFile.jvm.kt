/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2025 Forte Scarlet
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
@file:JvmName("JavaFiles")
@file:JvmMultifileClass

package love.forte.codegentle.java

import love.forte.codegentle.common.naming.appendTo
import love.forte.codegentle.common.naming.isEmpty
import love.forte.codegentle.common.naming.isNotEmpty
import love.forte.codegentle.common.naming.partSequence
import love.forte.codegentle.java.strategy.JavaWriteStrategy
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists

@JvmOverloads
@Throws(IOException::class)
public fun JavaFile.writeTo(
    directory: Path,
    charset: Charset = Charsets.UTF_8,
    strategy: JavaWriteStrategy = JavaWriteStrategy
) {
    require(directory.notExists() || directory.isDirectory()) {
        "Path $directory does not exist or is not a directory."
    }

    var outputDir = directory

    if (packageName.isNotEmpty()) {
        for (packagePart in packageName.partSequence) {
            outputDir = outputDir.resolve(packagePart)
        }
        outputDir.createDirectories()
    }

    val outputPath = outputDir.resolve(type.name + ".java")

    outputPath.bufferedWriter(charset = charset).use { writer ->
        writeTo(out = writer, strategy = strategy)
    }
}

@Throws(IOException::class)
public fun JavaFile.writeTo(
    directory: File,
    charset: Charset = Charsets.UTF_8,
    strategy: JavaWriteStrategy = JavaWriteStrategy
) {
    writeTo(directory.toPath(), charset, strategy)
}

// APT

/**
 * Create a [JavaFileObject] from a [JavaFile].
 */
public fun JavaFile.toJavaFileObject(): JavaFileObject {
    val name = if (packageName.isEmpty()) {
        type.name
    } else {
        buildString {
            packageName.appendTo(this, "/")
            append('/')
            append(type.name)
        }
        // packageName.replace('.', '/') + '/' + type.name
    }

    val uri = URI.create(name + JavaFileObject.Kind.SOURCE.extension)

    val lastModified = System.currentTimeMillis()

    return object : SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
        override fun getCharContent(ignoreEncodingErrors: Boolean): String {
            return this@toJavaFileObject.toString()
        }

        override fun openInputStream(): InputStream {
            return ByteArrayInputStream(getCharContent(true).toByteArray(StandardCharsets.UTF_8))
        }

        override fun getLastModified(): Long = lastModified
    }
}

@Throws(IOException::class)
public fun JavaFile.writeTo(
    filer: Filer,
    strategy: JavaWriteStrategy = JavaWriteStrategy,
    originatingElements: Iterable<Element>
) {
    writeTo(filer, strategy, *originatingElements.toList().toTypedArray())
}


/**
 * Write [JavaFile] to [Filer].
 *
 * @param originatingElements see `originatingElements` in [Filer.createSourceFile]
 */
@Throws(IOException::class)
public fun JavaFile.writeTo(
    filer: Filer,
    strategy: JavaWriteStrategy = JavaWriteStrategy,
    vararg originatingElements: Element
) {
    val fileName = if (packageName.isEmpty()) {
        type.name
    } else {
        buildString {
            packageName.appendTo(this)
            append('.')
            append(type.name)
        }
    }

    val filerSourceFile = filer.createSourceFile(fileName, *originatingElements)

    try {
        filerSourceFile.openWriter().use { writer ->
            writeTo(out = writer, strategy = strategy)
        }
    } catch (e: Exception) {
        runCatching {
            filerSourceFile.delete()
        }

        throw e
    }
}
