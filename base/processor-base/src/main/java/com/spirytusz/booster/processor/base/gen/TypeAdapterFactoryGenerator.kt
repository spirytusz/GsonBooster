package com.spirytusz.booster.processor.base.gen

import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.squareup.kotlinpoet.TypeSpec

/**
 * [TypeAdapterFactory]类生成器
 */
interface TypeAdapterFactoryGenerator {

    /**
     * 根据数据类与[TypeAdapter]之间的映射关系集合[classToTypeAdapters]生成一个[TypeAdapterFactory]
     *
     * @param typeAdapterFactoryName 生成类的权限定名
     * @param classToTypeAdapters 数据类与[TypeAdapter]之间的映射关系集合
     */
    fun generate(
        typeAdapterFactoryName: String,
        classToTypeAdapters: Set<Pair<KtType, KtType>>
    ): TypeSpec

    interface Factory {

        fun create(logger: MessageLogger): TypeAdapterFactoryGenerator
    }
}