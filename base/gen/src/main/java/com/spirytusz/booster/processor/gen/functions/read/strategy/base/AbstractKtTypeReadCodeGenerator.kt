package com.spirytusz.booster.processor.gen.functions.read.strategy.base

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.const.Const.Naming.READER
import com.squareup.kotlinpoet.CodeBlock

abstract class AbstractKtTypeReadCodeGenerator(
    protected val logger: MessageLogger,
    protected val config: TypeAdapterClassGenConfig
) : KtTypeReadCodeGenerator {

    protected val nullSafe: Boolean = config.nullSafe

    final override fun generate(
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.Builder()

        codeBlockBuilder.beginControlFlow("when ($READER.peek())")

        codeBlockBuilder.beginControlFlow(
            "%T.${ktType.jsonTokenName.jsonToken} -> ",
            JsonToken::class
        )
        enterExpectTokenBlock(codeBlockBuilder, ktType, codegenHook)
        codeBlockBuilder.endControlFlow()

        codeBlockBuilder.beginControlFlow("%T.NULL ->", JsonToken::class)
        enterNullTokenBlock(codeBlockBuilder, ktType, codegenHook)
        codeBlockBuilder.endControlFlow()

        codeBlockBuilder.beginControlFlow("else -> ")
        enterOtherTokenBlock(codeBlockBuilder, ktType, codegenHook)
        codeBlockBuilder.endControlFlow()

        codeBlockBuilder.endControlFlow()

        return codeBlockBuilder.build()
    }

    abstract fun enterExpectTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    )

    abstract fun enterNullTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    )

    abstract fun enterOtherTokenBlock(
        codeBlockBuilder: CodeBlock.Builder,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    )
}