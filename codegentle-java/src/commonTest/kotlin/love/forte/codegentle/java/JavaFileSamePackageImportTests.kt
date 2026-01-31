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
package love.forte.codegentle.java

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.JavaSimpleTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for same package import optimization in JavaFile
 */
class JavaFileSamePackageImportTests {

    @Test
    fun testSamePackageImportSkipped() {
        val packageName = "com.example.test".parseToPackageName()
        
        // Create fields with classes from same and different packages
        val samePackageField = JavaFieldSpec(ClassName("com.example.test", "SamePackageClass").ref(), "samePackageField")
        val differentPackageField = JavaFieldSpec(ClassName("com.example.other", "OtherClass").ref(), "otherField")
        
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "TestClass") {
            addField(samePackageField)
            addField(differentPackageField)
        }
        
        val javaFile = JavaFile(packageName, typeSpec)
        
        val generatedCode = javaFile.writeToJavaString()
        
        // Should NOT import same-package class
        assertFalse(
            generatedCode.contains("import com.example.test.SamePackageClass"),
            "Should not import class from same package: $generatedCode"
        )
        
        // Different-package class may be fully qualified or imported
        // The key test is that same-package class is NOT imported
        assertTrue(
            generatedCode.contains("OtherClass") || generatedCode.contains("com.example.other.OtherClass"),
            "Should contain reference to different-package class: $generatedCode"
        )
        
        // Both class names should still be used in the code
        assertTrue(
            generatedCode.contains("SamePackageClass samePackageField"),
            "Should use same-package class name without qualification: $generatedCode"
        )
        
        assertTrue(
            generatedCode.contains("otherField"),
            "Should contain the field reference: $generatedCode"
        )
    }

    @Test
    fun testEmptyPackageBehaviorUnchanged() {
        val emptyPackage = "".parseToPackageName()
        val emptyPackageField = JavaFieldSpec(ClassName("", "EmptyPackageClass").ref(), "field")
        
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "TestClass") {
            addField(emptyPackageField)
        }
        
        val javaFile = JavaFile(emptyPackage, typeSpec)
        
        val generatedCode = javaFile.writeToJavaString()
        
        // Should not have any imports for empty package classes
        assertFalse(
            generatedCode.contains("import EmptyPackageClass"),
            "Should not import class from empty package: $generatedCode"
        )
    }
    
    @Test
    fun testMultipleSamePackageClassesSkipped() {
        val packageName = "com.example.test".parseToPackageName()
        
        // Create multiple fields with classes from same package
        val field1 = JavaFieldSpec(ClassName("com.example.test", "ClassOne").ref(), "fieldOne")
        val field2 = JavaFieldSpec(ClassName("com.example.test", "ClassTwo").ref(), "fieldTwo")
        val field3 = JavaFieldSpec(ClassName("com.example.other", "ExternalClass").ref(), "externalField")
        
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "TestClass") {
            addField(field1)
            addField(field2)
            addField(field3)
        }
        
        val javaFile = JavaFile(packageName, typeSpec)
        
        val generatedCode = javaFile.writeToJavaString()
        
        // Should NOT import any same-package classes
        assertFalse(
            generatedCode.contains("import com.example.test.ClassOne"),
            "Should not import ClassOne from same package: $generatedCode"
        )
        
        assertFalse(
            generatedCode.contains("import com.example.test.ClassTwo"),
            "Should not import ClassTwo from same package: $generatedCode"
        )
        
        // External package class may be fully qualified or imported
        // The key test is that same-package classes are NOT imported
        assertTrue(
            generatedCode.contains("ExternalClass") || generatedCode.contains("com.example.other.ExternalClass"),
            "Should contain reference to external package class: $generatedCode"
        )
        
        // All field references should be present
        assertTrue(
            generatedCode.contains("fieldOne") && 
            generatedCode.contains("fieldTwo") && 
            generatedCode.contains("externalField"),
            "Should contain all field references: $generatedCode"
        )
        
        // Same-package classes should be used without qualification
        assertTrue(
            generatedCode.contains("ClassOne fieldOne") && 
            generatedCode.contains("ClassTwo fieldTwo"),
            "Should use same-package class names without qualification: $generatedCode"
        )
    }
}
