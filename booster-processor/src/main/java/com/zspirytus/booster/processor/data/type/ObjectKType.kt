package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName

data class ObjectKType(
    val className: ClassName
) : KType(className) {

    private val adapterFieldNamePrefix by lazy {
        val simpleName = (typeName as ClassName).simpleName
        simpleName.replaceFirst(simpleName.first(), simpleName.first().toLowerCase())
    }

    override val adapterFieldName: String
        get() = "${adapterFieldNamePrefix}TypeAdapter"
}