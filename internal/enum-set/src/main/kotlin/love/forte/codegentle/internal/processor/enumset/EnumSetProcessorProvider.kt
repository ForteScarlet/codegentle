package love.forte.codegentle.internal.processor.enumset

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Provider for the EnumSet processor.
 * This class is used by KSP to create instances of the EnumSetProcessor.
 *
 * @author ForteScarlet
 */
class EnumSetProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EnumSetProcessor(environment)
    }
}
