package com.spirytusz.booster.processor.data.type

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.const.TYPE_ADAPTER_NAME
import com.spirytusz.booster.processor.extensions.firstCharLowerCase
import com.spirytusz.booster.processor.extensions.kotlinType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asClassName

/**
 * 代表一个泛型为[PrimitiveKType] 或 [ObjectKType]的集合类型
 *
 * @param parameterizedTypeName 变量类型 [rawType] + [genericType]
 * @param rawType 集合类型
 * @param genericType 泛型类型
 */
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

    fun isSet(): Boolean {
        val kotlinSet = Set::class.java.asClassName()
        val javaSet = ClassName("java.lang", "Set")
        return rawType == kotlinSet || rawType == javaSet
    }
}