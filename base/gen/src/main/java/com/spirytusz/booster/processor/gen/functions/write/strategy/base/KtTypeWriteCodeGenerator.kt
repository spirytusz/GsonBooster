package com.spirytusz.booster.processor.gen.functions.write.strategy.base

import com.spirytusz.booster.processor.base.data.type.KtType
import com.squareup.kotlinpoet.CodeBlock

interface KtTypeWriteCodeGenerator {

    fun generate(
        fieldName: String,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ): CodeBlock
}