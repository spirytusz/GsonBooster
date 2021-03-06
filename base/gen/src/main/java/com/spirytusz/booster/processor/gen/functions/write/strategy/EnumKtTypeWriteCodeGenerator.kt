package com.spirytusz.booster.processor.gen.functions.write.strategy

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.const.Const.Naming.WRITER
import com.spirytusz.booster.processor.gen.functions.write.strategy.base.AbstractKtTypeWriteCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

internal class EnumKtTypeWriteCodeGenerator(
    logger: MessageLogger,
    config: TypeAdapterClassGenConfig
) : AbstractKtTypeWriteCodeGenerator(logger, config) {
    override fun realGenerate(
        fieldName: String,
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        tempFieldName: String
    ) {
        codeBlockBuilder.addStatement("$WRITER.value($tempFieldName.name)")
    }
}