package com.spirytusz.booster.processor.gen.functions.write.strategy

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.contract.Constants.Naming.WRITER
import com.spirytusz.booster.processor.gen.extensions.firstChatUpperCase
import com.spirytusz.booster.processor.gen.extensions.flatten
import com.spirytusz.booster.processor.gen.functions.write.strategy.base.AbstractKtTypeWriteCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

internal class MapKtTypeWriteCodeGenerator(
    logger: MessageLogger,
    config: TypeAdapterClassGenConfig
) : AbstractKtTypeWriteCodeGenerator(logger, config) {
    override fun realGenerate(
        fieldName: String,
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        tempFieldName: String
    ) {
        val valueGeneric = ktType.generics[1]

        codeBlockBuilder.addStatement("$WRITER.beginObject()")

        val loopKeyFieldName = "key" + valueGeneric.flatten().firstChatUpperCase()
        val loopValueFieldName = "value" + valueGeneric.flatten().firstChatUpperCase()
        codeBlockBuilder.beginControlFlow("for (($loopKeyFieldName, $loopValueFieldName) in $tempFieldName)")

        val writeCodeGenerator = KtTypeWriteCodeGeneratorImpl(logger, config)
        val genericCodeBlock = writeCodeGenerator.generate(
            fieldName,
            valueGeneric
        ) { genericCodeBlockBuilder, genericTempFieldName ->
            genericCodeBlockBuilder.addStatement("$WRITER.name($loopKeyFieldName)")
            genericCodeBlockBuilder.addStatement("val $genericTempFieldName = $loopValueFieldName")
        }
        codeBlockBuilder.add(genericCodeBlock)

        codeBlockBuilder.endControlFlow()

        codeBlockBuilder.addStatement("$WRITER.endObject()")
    }
}