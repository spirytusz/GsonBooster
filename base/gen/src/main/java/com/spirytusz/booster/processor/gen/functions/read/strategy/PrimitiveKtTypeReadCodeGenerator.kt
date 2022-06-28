package com.spirytusz.booster.processor.gen.functions.read.strategy

import com.google.gson.internal.bind.TypeAdapters
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.extensions.kotlinPrimitiveType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.contract.Constants.Naming.READER
import com.spirytusz.booster.processor.gen.extensions.getReadingTempFieldName
import com.spirytusz.booster.processor.gen.functions.read.strategy.base.AbstractKtTypeReadCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

internal class PrimitiveKtTypeReadCodeGenerator(
    logger: MessageLogger,
    config: TypeAdapterClassGenConfig
) : AbstractKtTypeReadCodeGenerator(logger, config) {

    override fun enterExpectTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ) {
        val tempFieldName = ktType.getReadingTempFieldName()
        val nextFuncExp = ktType.jsonTokenName.nextFuncExp
        codeBlockBuilder.addStatement(
            "val $tempFieldName: %T = $READER.$nextFuncExp",
            ktType.asTypeName().kotlinPrimitiveType()
        )
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
                codeBlockBuilder.addStatement(
                    "val $tempFieldName: %T = null",
                    ktType.asTypeName().kotlinPrimitiveType()
                )
                codegenHook.invoke(codeBlockBuilder, tempFieldName)
            }
            nullSafe && !strictType -> {
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
        if (strictType) {
            generateExpectTokenButTokenBlock(codeBlockBuilder, ktType)
        } else {
            val gsonInternalTypeAdapterName = getGsonInternalTypeAdapterName(ktType)
            val tempFieldName = ktType.getReadingTempFieldName()
            codeBlockBuilder.addStatement(
                "val $tempFieldName: %T = %T.%L.read($READER) as %T",
                ktType.asTypeName().kotlinPrimitiveType(),
                TypeAdapters::class,
                gsonInternalTypeAdapterName,
                ktType.asTypeName().kotlinPrimitiveType()
            )
            codegenHook.invoke(codeBlockBuilder, tempFieldName)
        }
    }

    private fun getGsonInternalTypeAdapterName(ktType: KtType): String {
        return when (ktType.jsonTokenName) {
            JsonTokenName.INT -> "INTEGER"
            JsonTokenName.LONG -> "LONG"
            JsonTokenName.FLOAT -> "FLOAT"
            JsonTokenName.DOUBLE -> "DOUBLE"
            JsonTokenName.STRING -> "STRING"
            JsonTokenName.BOOLEAN -> "BOOLEAN"
            else -> {
                logger.error("unexpected ktType", ktType)
                throw IllegalArgumentException("unexpected ktType ${ktType.toReadableString()}")
            }
        }
    }
}