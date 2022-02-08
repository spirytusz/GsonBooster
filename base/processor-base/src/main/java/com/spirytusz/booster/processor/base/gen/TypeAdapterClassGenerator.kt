package com.spirytusz.booster.processor.base.gen

import com.google.gson.TypeAdapter
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.squareup.kotlinpoet.TypeSpec

/**
 * [TypeAdapter]类生成器
 */
interface TypeAdapterClassGenerator {

    /**
     * 设置类过滤器。
     * 如果在生成的过程中，遇到的字段类型在[classes]中，说明这个类已经生成过TypeAdapter了，
     * 直接调用这个类对应TypeAdapter的构造方法来获取TypeAdapter实例；
     * 否则，使用gson.getAdapter方法来获取对应TypeAdapter的实例。
     */
    fun setClassFilter(classes: Set<KtType>)

    /**
     * 根据扫描结果[scanner]，生成一个TypeAdapter
     *
     * @param scanner 类扫描器
     * @param config 逻辑代码生成上的配置
     */
    fun generate(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig = TypeAdapterClassGenConfig()
    ): TypeSpec
}