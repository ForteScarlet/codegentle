/*
 * Copyright (C) 2025 Forte Scarlet
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
package love.forte.codegentle.kotlin.ksp

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import love.forte.codegentle.kotlin.spec.KotlinFunctionCollector
import love.forte.codegentle.kotlin.spec.KotlinPropertyCollector
import love.forte.codegentle.kotlin.spec.KotlinSecondaryConstructorCollector

/**
 * Checks if a KSP [KSFunctionDeclaration] is a constructor.
 *
 * @return True if the function is a constructor, false otherwise
 */
// internal fun KSFunctionDeclaration.isConstructor(): Boolean {
//     return simpleName.asString() == "<init>"
// }

internal inline fun KSClassDeclaration.transformFunctionsTo(
    functionCollector: KotlinFunctionCollector<*>,
    functions: KSClassDeclaration.() -> Iterator<KSFunctionDeclaration> = { getAllFunctions().iterator() },
    onSecondaryConstructor: (KSFunctionDeclaration) -> Unit = {}
) {
    val pri = primaryConstructor

    functions().forEach { function ->
        if (!function.isConstructor()) {
            functionCollector.addFunction(function.toKotlinFunctionSpec())
        } else if (function != pri) {
            onSecondaryConstructor(function)
        }
    }
}

internal inline fun <C> KSClassDeclaration.transformFunctionsAndSecondaryConstructorsTo(
    collector: C,
    functions: KSClassDeclaration.() -> Iterator<KSFunctionDeclaration> = { getAllFunctions().iterator() },
) where C : KotlinFunctionCollector<*>, C : KotlinSecondaryConstructorCollector<*> {
    transformFunctionsTo(
        functionCollector = collector,
        functions = functions,
        onSecondaryConstructor = { function ->
            collector.addSecondaryConstructor(function.toKotlinConstructorSpec())
        }
    )
}

internal fun KSClassDeclaration.transformPropertiesTo(
    propertyCollector: KotlinPropertyCollector<*>,
    properties: KSClassDeclaration.() -> Iterator<KSPropertyDeclaration> = { getAllProperties().iterator() }
) {
    properties().forEach { property ->
        propertyCollector.addProperty(property.toKotlinPropertySpec())
    }
}
