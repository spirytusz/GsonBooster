package com.spirytusz.booster.processor.base.gen

import com.google.gson.TypeAdapter
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.squareup.kotlinpoet.TypeSpec

/**
 * [TypeAdapter]类生成器
 */
interface TypeAdapterGenerator {

    /**
     * 根据扫描结果[scanner]，生成一个TypeAdapter
     *
     * @param scanner 类扫描器
     * @param config 逻辑代码生成上的配置
     */
    fun generate(
        scanner: ClassScanner,
        classFilter: Set<KtType>,
        config: TypeAdapterClassGenConfig = TypeAdapterClassGenConfig()
    ): TypeSpec

    interface Factory {
        fun create(logger: MessageLogger): TypeAdapterGenerator
    }
}