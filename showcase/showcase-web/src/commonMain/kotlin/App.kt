import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import code.CodeBlock
import code.rememberCodeBlockHoverState
import love.forte.codegentle.common.code.addCode
import love.forte.codegentle.common.code.emitName
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.spec.*

@Composable
fun App() {
    // Show code
    val expectCode = """
        /**
         * This is a class.
         */
        class MyClass {
            /**
             * This is a property.
             */
            val myProperty: com.example.MyClass

            /**
             * This is a function.
             */
            fun myFunction(): com.example.MyClass = myProperty
        }
    """.trimIndent()

    val actualCode = remember { actualCode() }
    val hoverState = rememberCodeBlockHoverState()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxHeight()) {
            Row {
                CodeBlock(
                    code = expectCode,
                    language = "kotlin",
                    hoverEffectEnabled = true,
                    hoverSyncState = hoverState,
                    modifier = Modifier.weight(1f)
                )
                CodeBlock(
                    code = actualCode,
                    language = "kotlin",
                    hoverEffectEnabled = true,
                    hoverSyncState = hoverState,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}

private fun actualCode(): String {
    val className = ClassName("com.example", "MyClass")
    val typeRef = className.ref()

    val propertySpec = KotlinPropertySpec("myProperty", typeRef) {
        addDoc("This is a property.")
    }

    val functionSpec = KotlinFunctionSpec("myFunction", typeRef) {
        addDoc("This is a function.")
        addCode("return %V") {
            emitName("myProperty")
        }
    }

    val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass") {
        addDoc("This is a class.")
        addProperty(propertySpec)
        addFunction(functionSpec)
    }

    return typeSpec.writeToKotlinString()
}
