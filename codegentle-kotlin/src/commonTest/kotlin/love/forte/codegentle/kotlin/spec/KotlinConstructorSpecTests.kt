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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for [KotlinConstructorSpec].
 */
class KotlinConstructorSpecTests {

    @Test
    fun testBasicConstructor() {
        val constructorSpec = KotlinConstructorSpec { }

        assertNotNull(constructorSpec)
        assertEquals(emptySet<KotlinModifier>(), constructorSpec.modifiers)
        assertEquals(emptyList<KotlinValueParameterSpec>(), constructorSpec.parameters)
        assertNull(constructorSpec.constructorDelegation)
    }

    @Test
    fun testConstructorWithModifiers() {
        val constructorSpec = KotlinConstructorSpec {
            addModifier(KotlinModifier.PRIVATE)
        }

        assertEquals(setOf(KotlinModifier.PRIVATE), constructorSpec.modifiers)
    }

    @Test
    fun testConstructorWithParameters() {
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()

        val param1 = KotlinValueParameterSpec("param1", stringType)
        val param2 = KotlinValueParameterSpec("param2", intType) {
            defaultValue("0")
        }

        val constructorSpec = KotlinConstructorSpec {
            addParameter(param1)
            addParameter(param2)
        }

        assertEquals(2, constructorSpec.parameters.size)
        assertEquals("param1", constructorSpec.parameters[0].name)
        assertEquals("param2", constructorSpec.parameters[1].name)
    }

    @Test
    fun testConstructorWithThisDelegation() {
        val constructorSpec = KotlinConstructorSpec {
            thisConstructorDelegation {
                addArgument("\"default\"")
                addArgument("0")
            }
        }

        assertNotNull(constructorSpec.constructorDelegation)
        assertEquals(KotlinConstructorDelegation.Kind.THIS, constructorSpec.constructorDelegation?.kind)
        assertEquals(2, constructorSpec.constructorDelegation?.arguments?.size)
    }

    @Test
    fun testConstructorWithSuperDelegation() {
        val constructorSpec = KotlinConstructorSpec {
            superConstructorDelegation {
                addArgument("\"parent\"")
            }
        }

        assertNotNull(constructorSpec.constructorDelegation)
        assertEquals(KotlinConstructorDelegation.Kind.SUPER, constructorSpec.constructorDelegation?.kind)
        assertEquals(1, constructorSpec.constructorDelegation?.arguments?.size)
    }

    @Test
    fun testConstructorWithCode() {
        val constructorSpec = KotlinConstructorSpec {
            addCode("println(\"Constructor called\")")
        }

        assertNotNull(constructorSpec.code)
        assertEquals("println(\"Constructor called\")", constructorSpec.code.writeToKotlinString())
    }

    @Test
    fun testConstructorWithKDoc() {
        val constructorSpec = KotlinConstructorSpec {
            addDoc("This is a constructor.")
        }

        assertNotNull(constructorSpec.kDoc)
        assertEquals("This is a constructor.", constructorSpec.kDoc.writeToKotlinString())
    }
}
