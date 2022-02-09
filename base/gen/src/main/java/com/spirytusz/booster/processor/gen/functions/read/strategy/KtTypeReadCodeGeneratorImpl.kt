package com.spirytusz.booster.processor.gen.functions.read.strategy

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.functions.read.strategy.base.KtTypeReadCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

class KtTypeReadCodeGeneratorImpl(
    private val logger: MessageLogger,
    private val config: TypeAdapterClassGenConfig
) : KtTypeReadCodeGenerator {

    override fun generate(
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ): CodeBlock {
        return when {
            ktType.jsonTokenName.isPrimitive() -> {
                PrimitiveKtTypeReadCodeGenerator(logger, config).generate(ktType, codegenHook)
            }
            ktType.jsonTokenName.isObject() -> {
                ObjectKtTypeReadCodeGenerator(logger, config).generate(ktType, codegenHook)
            }
            ktType.jsonTokenName.isArray() -> {
                CollectionKtTypeReadCodeGenerator(logger, config).generate(ktType, codegenHook)
            }
            ktType.jsonTokenName.isMap() -> {
                MapKtTypeReadCodeGenerator(logger, config).generate(ktType, codegenHook)
            }
            ktType.jsonTokenName.isEnum() -> {
                EnumKtTypeReadCodeGenerator(logger, config).generate(ktType, codegenHook)
            }
            else -> throw IllegalStateException("Unexpected json token ${ktType.jsonTokenName}")
        }
    }
}