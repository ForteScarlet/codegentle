package test.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.kotlin.ksp.toKotlinFunctionSpec
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.strategy.DefaultKotlinWriteStrategy
import java.io.OutputStreamWriter
import kotlin.random.Random

class GenerateBackupProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val functions =
            resolver.getSymbolsWithAnnotation(GenerateBackup::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        val invalidList: List<KSAnnotated> = functions.filter { !it.validate() }
        val validList = functions.filter { it.validate() }

        if (validList.isEmpty()) {
            return invalidList
        }

        functions.forEach { function ->
            processFunction(function)
        }

        return invalidList
    }

    private fun processFunction(function: KSFunctionDeclaration) {
        val packageName = function.packageName.asString()
        val functionName = function.simpleName.asString()

        // 生成随机后缀
        val suffix = Random.nextInt(1000, 9999).toString()
        val backupFunctionName = "${functionName}_$suffix"

        logger.info("Generating backup for function: $functionName -> $backupFunctionName")

        try {
            // 使用 codegentle-kotlin-ksp 将 KSP 函数转换为 KotlinFunctionSpec
            println("original function: $function")
            function.parameters.forEach {
                println("\toriginal function parameter: $it")
                println("\toriginal function parameter.name: ${it.name?.asString()}")
                println("\toriginal function parameter.type: ${it.type}")
                println("\toriginal function parameter.type.annotations: ${it.type.annotations.toList()}")
                println("\toriginal function parameter.type.annotations: ${it.type.annotations.map { a -> a.annotationType }.toList()}")
                println("\toriginal function parameter.type.annotations.resolve(): ${it.type.resolve()}")
                println("\toriginal function parameter.type.annotations.resolve().annotations: ${it.type.resolve().annotations.toList()}")
            }
            val originalSpec = function.toKotlinFunctionSpec()

            // 创建备份函数 - 使用新名字重建
            val backupSpec = KotlinFunctionSpec.builder(backupFunctionName).apply {
                // 复制原函数的所有属性
                originalSpec.modifiers.forEach { addModifier(it) }
                originalSpec.annotations.filter { it.typeName.simpleName != "GenerateBackup" }.forEach { addAnnotation(it) }
                originalSpec.typeVariables.forEach { addTypeVariable(it) }
                originalSpec.parameters.forEach { addParameter(it) }
                originalSpec.returnType?.let { returns(it) }
                originalSpec.receiver?.let { receiver(it) }

                // 添加简单的函数体说明这是备份
                addCode("// This is a backup of $functionName\n")
                addCode("TODO(\"Implement backup function\")\n")
            }.build()

            // 生成文件
            generateFile(packageName, backupFunctionName, backupSpec)

            logger.info("Successfully generated backup function: $backupFunctionName")
            logger.info("  Parameters: ${originalSpec.parameters.map { it.name to it.typeRef }}")
        } catch (e: Exception) {
            logger.error("Failed to generate backup for $functionName: ${e.message}", function)
            e.printStackTrace()
        }
    }

    private fun generateFile(
        packageName: String,
        functionName: String,
        functionSpec: KotlinFunctionSpec
    ) {
        val fileName = "${functionName}Generated"

        // 创建 KotlinFile
        val kotlinFile = KotlinFile(packageName) {
            name(fileName)
            addFunction(functionSpec)
        }

        // 生成代码
        val file = codeGenerator.createNewFile(
            Dependencies(false),
            packageName,
            fileName
        )

        OutputStreamWriter(file, Charsets.UTF_8).use { writer ->
            kotlinFile.writeTo(writer, DefaultKotlinWriteStrategy())
        }
    }
}

class GenerateBackupProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return GenerateBackupProcessor(
            environment.codeGenerator,
            environment.logger
        )
    }
}
