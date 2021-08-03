package com.zspirytus.booster.processor.strategy.declare

import com.google.gson.TypeAdapter
import com.squareup.kotlinpoet.*
import com.zspirytus.booster.processor.const.GSON
import com.zspirytus.booster.processor.data.type.KType

internal class ObjectAdapterDeclareStrategy : IAdapterDeclareStrategy {

    var registerTypeAdapters = mapOf<String, ClassName>()

    override fun declare(kType: KType): PropertySpec? {
        val typeClassName = kType.typeName as ClassName
        val isRegisteredType = registerTypeAdapters.containsKey(typeClassName.canonicalName)
        val type = if (isRegisteredType) {
            registerTypeAdapters[typeClassName.canonicalName]
        } else {
            kType.typeName
        }
        val typeAdapterCodeBlock = if (isRegisteredType) {
            CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement("%T()", type)
                .endControlFlow()
                .build()
        } else {
            CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement("%L.getAdapter(%T::class.java)", GSON, type)
                .endControlFlow()
                .build()
        }
        val adapterType = with(ParameterizedTypeName.Companion) {
            TypeAdapter::class.asClassName().parameterizedBy(kType.typeName)
        }
        return PropertySpec.builder(kType.adapterFieldName, adapterType, KModifier.PRIVATE)
            .delegate(typeAdapterCodeBlock)
            .build()
    }
}