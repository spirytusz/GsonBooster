package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element

data class ObjectKType(
    val element: Element
) : KType(element) {

    private val adapterFieldNamePrefix by lazy {
        val simpleName = (typeName as ClassName).simpleName
        simpleName.replaceFirst(simpleName.first(), simpleName.first().toLowerCase())
    }

    override val adapterFieldName: String
        get() = "${adapterFieldNamePrefix}TypeAdapter"
}