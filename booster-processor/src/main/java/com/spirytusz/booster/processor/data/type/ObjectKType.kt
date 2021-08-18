package com.spirytusz.booster.processor.data.type

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.const.TYPE_ADAPTER_NAME
import com.spirytusz.booster.processor.extensions.firstCharLowerCase
import com.squareup.kotlinpoet.ClassName

/**
 * 代表一个不带泛型的Object类型
 *
 * @param className 类型名
 */
data class ObjectKType(
    val className: ClassName
) : KType(className) {

    private val adapterFieldNamePrefix by lazy {
        className.simpleName.firstCharLowerCase()
    }

    override val adapterFieldName: String
        get() = "${adapterFieldNamePrefix}$TYPE_ADAPTER_NAME"

    override val jsonTokenName: String
        get() = JsonToken.BEGIN_OBJECT.name
}