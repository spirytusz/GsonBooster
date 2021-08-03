package com.zspirytus.booster.processor.strategy.declare

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.zspirytus.booster.processor.const.GSON
import com.zspirytus.booster.processor.data.type.KType
import com.zspirytus.booster.processor.extensions.kotlinType

internal class BackoffAdapterDeclareStrategy : IAdapterDeclareStrategy {
    override fun declare(kType: KType): PropertySpec? {
        val typeAdapterCodeBlock = CodeBlock.Builder()
            .beginControlFlow("lazy")
            .addStatement("%L.getAdapter(object: %T<%L>() {})", GSON, TypeToken::class.java, kType.typeName.kotlinType())
            .endControlFlow()
            .build()
        val adapterType = with(ParameterizedTypeName.Companion) {
            TypeAdapter::class.asClassName().parameterizedBy(kType.typeName.kotlinType())
        }
        return PropertySpec.builder(kType.adapterFieldName, adapterType, KModifier.PRIVATE)
            .delegate(typeAdapterCodeBlock)
            .build()
    }
}