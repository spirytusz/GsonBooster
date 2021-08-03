package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element

data class CollectionKType(
    val element: Element,
    val rawType: ClassName,
    val genericType: ClassName
) : KType(element) {

    private val adapterFieldNamePrefix by lazy {
        val simpleName = genericType.simpleName
        simpleName.replaceFirst(simpleName.first(), simpleName.first().toLowerCase())
    }

    override val adapterFieldName: String
        get() = "${adapterFieldNamePrefix}TypeAdapter"
}