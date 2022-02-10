package com.spirytusz.booster.processor.gen.functions.write.strategy

import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.functions.write.strategy.base.KtTypeWriteCodeGenerator
import com.squareup.kotlinpoet.CodeBlock

class KtTypeWriteCodeGeneratorImpl(
    private val logger: MessageLogger,
    private val config: TypeAdapterClassGenConfig
) : KtTypeWriteCodeGenerator {
    override fun generate(
        fieldName: String,
        ktType: KtType,
        codegenHook: (CodeBlock.Builder, String) -> Unit
    ): CodeBlock {
        return when {
            ktType.jsonTokenName.isPrimitive() -> {
                PrimitiveKtTypeWriteCodeGenerator(logger, config).generate(
                    fieldName,
                    ktType,
                    codegenHook
                )
            }
            ktType.jsonTokenName.isObject() -> {
                ObjectKtTypeWriteCodeGenerator(logger, config).generate(
                    fieldName,
                    ktType,
                    codegenHook
                )
            }
            ktType.jsonTokenName.isEnum() -> {
                EnumKtTypeWriteCodeGenerator(logger, config).generate(
                    fieldName,
                    ktType,
                    codegenHook
                )
            }
            ktType.jsonTokenName.isArray() -> {
                CollectionKtTypeWriteCodeGenerator(logger, config).generate(
                    fieldName,
                    ktType,
                    codegenHook
                )
            }
            ktType.jsonTokenName.isMap() -> {
                MapKtTypeWriteCodeGenerator(logger, config).generate(fieldName, ktType, codegenHook)
            }
            else -> throw IllegalStateException("Unexpected json token ${ktType.jsonTokenName}")
        }
    }
}