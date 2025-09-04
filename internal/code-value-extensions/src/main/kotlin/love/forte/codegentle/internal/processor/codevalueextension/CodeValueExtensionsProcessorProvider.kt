package love.forte.codegentle.internal.processor.codevalueextension

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 *
 * @author ForteScarlet
 */
class CodeValueExtensionsProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CodeValueExtensionsProcessor(environment)
    }
}
