package com.spirytusz.booster.processor.gen.functions.write

import com.google.gson.stream.JsonWriter
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.contract.Constants.Naming.OBJECT
import com.spirytusz.booster.contract.Constants.Naming.WRITER
import com.spirytusz.booster.processor.gen.functions.write.strategy.KtTypeWriteCodeGeneratorImpl
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

internal class WriteFunctionGenerator(private val logger: MessageLogger) {

    fun generate(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig
    ): FunSpec {
        val className = scanner.classKtType.asTypeName() as ClassName
        val writeFunc = FunSpec.builder("write")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(WRITER, JsonWriter::class)
            .addParameter(OBJECT, className.copy(nullable = true))

        writeFunc.beginControlFlow("if ($OBJECT == null)")
        writeFunc.addStatement("$WRITER.nullValue()")
        writeFunc.addStatement("return")
        writeFunc.endControlFlow()

        writeFunc.addStatement("$WRITER.beginObject()")
        scanner.ktFields.filter { it.declarationScope != DeclarationScope.SUPER_INTERFACE }
            .forEach {
                val writeCodeGenerator = KtTypeWriteCodeGeneratorImpl(logger, config)
                val codeBlock = writeCodeGenerator.generate(
                    it.fieldName,
                    it.ktType
                ) { tempCodeBlock, tempFieldName ->
                    tempCodeBlock.addStatement("$WRITER.name(%S)", it.keys.first())
                    tempCodeBlock.addStatement("val $tempFieldName = $OBJECT.${it.fieldName}")
                }
                writeFunc.addCode(codeBlock)
            }
        writeFunc.addStatement("$WRITER.endObject()")

        return writeFunc.build()
    }
}