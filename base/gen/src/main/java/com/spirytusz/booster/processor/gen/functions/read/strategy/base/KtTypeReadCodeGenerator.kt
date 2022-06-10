package com.spirytusz.booster.processor.gen.functions.read.strategy.base

import com.spirytusz.booster.processor.base.data.type.KtType
import com.squareup.kotlinpoet.CodeBlock

interface KtTypeReadCodeGenerator {

    fun generate(
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ): CodeBlock
}