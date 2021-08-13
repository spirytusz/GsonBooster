package com.spirytusz.booster.processor.data.type

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.ClassName
import com.spirytusz.booster.processor.const.TYPE_ADAPTER_NAME
import com.spirytusz.booster.processor.extensions.firstCharLowerCase

data class EnumKType(
    val className: ClassName
) : KType(className) {
    private val adapterFieldNamePrefix by lazy {
        className.simpleName.firstCharLowerCase()
    }

    override val adapterFieldName: String
        get() = "${adapterFieldNamePrefix}$TYPE_ADAPTER_NAME"

    override val jsonTokenName: String
        get() = JsonToken.STRING.name
}