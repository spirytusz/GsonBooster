package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.zspirytus.booster.processor.const.TYPE_ADAPTER_NAME
import com.zspirytus.booster.processor.extensions.kotlinType

data class CollectionKType(
    val parameterizedTypeName: ParameterizedTypeName,
    val rawType: ClassName,
    val genericType: ClassName
) : KType(parameterizedTypeName) {

    private val _adapterFieldName by lazy {
        if (PrimitiveKType.isPrimitive(genericType.kotlinType())) {
            ""
        } else {
            var simpleName = genericType.simpleName
            simpleName =
                simpleName.replaceFirst(simpleName.first(), simpleName.first().toLowerCase())
            "${simpleName}$TYPE_ADAPTER_NAME"
        }
    }

    override val adapterFieldName: String
        get() = _adapterFieldName
}