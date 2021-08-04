package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.zspirytus.booster.processor.const.TYPE_ADAPTER_NAME
import com.zspirytus.booster.processor.extensions.firstCharLowerCase

data class ObjectKType(
    val className: ClassName
) : KType(className) {

    private val adapterFieldNamePrefix by lazy {
        (typeName as ClassName).simpleName.firstCharLowerCase()
    }

    override val adapterFieldName: String
        get() = "${adapterFieldNamePrefix}$TYPE_ADAPTER_NAME"
}