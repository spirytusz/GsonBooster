package com.spirytusz.booster.processor.gen.functions.write.strategy

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.const.Const.Naming.WRITER
import com.spirytusz.booster.processor.gen.extensions.firstCharLowerCase
import com.spirytusz.booster.processor.gen.extensions.flatten
import com.spirytusz.booster.processor.gen.functions.write.strategy.base.AbstractKtTypeWriteCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

internal class CollectionKtTypeWriteCodeGenerator(
    logger: MessageLogger,
    config: TypeAdapterClassGenConfig
) : AbstractKtTypeWriteCodeGenerator(logger, config) {
    override fun realGenerate(
        fieldName: String,
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        tempFieldName: String
    ) {
        val generic = ktType.generics.first()

        codeBlockBuilder.addStatement("$WRITER.beginArray()")

        val loopFieldName = generic.flatten().firstCharLowerCase()
        codeBlockBuilder.beginControlFlow("for ($loopFieldName in $tempFieldName)")
        val writeCodeGenerator = KtTypeWriteCodeGeneratorImpl(logger, config)
        val genericCodeBlock = writeCodeGenerator.generate(
            fieldName,
            generic
        ) { genericCodeBlockBuilder, genericTempFieldName ->
            genericCodeBlockBuilder.addStatement("val $genericTempFieldName = $loopFieldName")
        }
        codeBlockBuilder.add(genericCodeBlock)
        codeBlockBuilder.endControlFlow()
        codeBlockBuilder.addStatement("$WRITER.endArray()")
    }
}