package com.spirytusz.booster.processor.gen.functions.read.strategy

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.const.Const.Naming.READER
import com.spirytusz.booster.processor.gen.extensions.getReadingTempFieldName
import com.spirytusz.booster.processor.gen.functions.read.strategy.base.AbstractKtTypeReadCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

internal class CollectionKtTypeReadCodeGenerator(
    logger: MessageLogger,
    config: TypeAdapterClassGenConfig
) : AbstractKtTypeReadCodeGenerator(logger, config) {

    override fun enterExpectTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ) {
        val tempFieldName = ktType.getReadingTempFieldName()
        val initializer = getCollectionInitializer(ktType)
        val generic = ktType.generics.first()
        codeBlockBuilder.addStatement(
            "val $tempFieldName = $initializer<%T>()",
            generic.asTypeName(ignoreVariance = true)
        )

        codeBlockBuilder.addStatement("$READER.beginArray()")
        codeBlockBuilder.beginControlFlow("while ($READER.hasNext())")

        val genericReadCodeGenerator = KtTypeReadCodeGeneratorImpl(logger, config)
        val genericReadCodeBlock =
            genericReadCodeGenerator.generate(generic) { genericCodeBlockBuilder, genericTempFieldName ->
                genericCodeBlockBuilder.addStatement("$tempFieldName.add($genericTempFieldName)")
            }
        codeBlockBuilder.add(genericReadCodeBlock)
        codeBlockBuilder.endControlFlow()
        codeBlockBuilder.addStatement("$READER.endArray()")
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
                codegenHook.invoke(codeBlockBuilder, "null")
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

    private fun getCollectionInitializer(ktType: KtType): String {
        return when (val jsonTokenName = ktType.jsonTokenName) {
            JsonTokenName.LIST -> "mutableListOf"
            JsonTokenName.SET -> "mutableSetOf"
            JsonTokenName.JAVA_LIST -> "java.util.ArrayList"
            JsonTokenName.JAVA_SET -> "java.util.LinkedHashSet"
            else -> throw IllegalStateException("Unexpected json token $jsonTokenName")
        }
    }
}