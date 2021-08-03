package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.zspirytus.booster.processor.extensions.kotlinType
import javax.lang.model.element.Element

data class CollectionKType(
    val element: Element,
    val rawType: ClassName,
    val genericType: ClassName
) : KType(element) {

    private val _adapterFieldName by lazy {
        if (PrimitiveKType.isPrimitive(genericType.kotlinType())) {
            ""
        } else {
            var simpleName = genericType.simpleName
            simpleName = simpleName.replaceFirst(simpleName.first(), simpleName.first().toLowerCase())
            "${simpleName}TypeAdapter"
        }
    }

    override val adapterFieldName: String
        get() = _adapterFieldName
}