package com.spirytusz.booster.processor.gen.functions.write.strategy.base

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.const.Const.Naming.WRITER
import com.spirytusz.booster.processor.gen.extensions.getWritingTempFieldName
import com.squareup.kotlinpoet.CodeBlock

abstract class AbstractKtTypeWriteCodeGenerator(
    protected val logger: MessageLogger,
    protected val config: TypeAdapterClassGenConfig
) : KtTypeWriteCodeGenerator {

    final override fun generate(
        fieldName: String,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.Builder()
        val tempFieldName = ktType.getWritingTempFieldName(fieldName)
        codegenHook.invoke(codeBlockBuilder, tempFieldName)
        if (ktType.nullable) {
            codeBlockBuilder.beginControlFlow("if ($tempFieldName == null)")
            codeBlockBuilder.addStatement("$WRITER.nullValue()")
            codeBlockBuilder.endControlFlow()

            codeBlockBuilder.beginControlFlow("else")
            realGenerate(fieldName, codeBlockBuilder, ktType, tempFieldName)
            codeBlockBuilder.endControlFlow()
        } else {
            realGenerate(fieldName, codeBlockBuilder, ktType, tempFieldName)
        }
        return codeBlockBuilder.build()
    }

    abstract fun realGenerate(
        fieldName: String,
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        tempFieldName: String
    )
}