package com.zspirytus.booster.processor.data.type

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.zspirytus.booster.processor.const.TYPE_ADAPTER_NAME
import com.zspirytus.booster.processor.extensions.firstCharLowerCase
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
            "${genericType.simpleName}$TYPE_ADAPTER_NAME".firstCharLowerCase()
        }
    }

    override val adapterFieldName: String
        get() = _adapterFieldName

    override val jsonTokenName: String
        get() = JsonToken.BEGIN_ARRAY.name
}