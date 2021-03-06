package com.spirytusz.booster.processor.gen.functions.read.strategy

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.const.Const.Naming.READER
import com.spirytusz.booster.processor.gen.extensions.getReadingTempFieldName
import com.spirytusz.booster.processor.gen.extensions.getTypeAdapterFieldName
import com.spirytusz.booster.processor.gen.functions.read.strategy.base.AbstractKtTypeReadCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

internal class ObjectKtTypeReadCodeGenerator(
    logger: MessageLogger,
    config: TypeAdapterClassGenConfig
) : AbstractKtTypeReadCodeGenerator(logger, config) {

    override fun enterExpectTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ) {
        val tempFieldName = ktType.getReadingTempFieldName()
        val typeAdapterFieldName = ktType.getTypeAdapterFieldName()
        codeBlockBuilder.addStatement("val $tempFieldName = $typeAdapterFieldName.read($READER)")
        codegenHook.invoke(codeBlockBuilder, tempFieldName)
    }

    override fun enterNullTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ) {
        when {
            ktType.nullable -> {
                codeBlockBuilder.addStatement("$READER.nextNull()")

                val tempFieldName = ktType.getReadingTempFieldName()
                codeBlockBuilder.addStatement("val $tempFieldName: %T = null", ktType.asTypeName())
                codegenHook.invoke(codeBlockBuilder, tempFieldName)
            }
            nullSafe -> {
                codeBlockBuilder.addStatement("$READER.skipValue()")
            }
            else -> {
                generateExpectTokenButTokenBlock(codeBlockBuilder, ktType)
            }
        }
    }

    override fun enterOtherTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ) {
        generateExpectTokenButTokenBlock(codeBlockBuilder, ktType)
    }
}