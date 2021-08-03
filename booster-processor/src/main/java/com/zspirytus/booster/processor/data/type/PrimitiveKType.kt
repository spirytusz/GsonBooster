package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import com.zspirytus.booster.processor.extensions.kotlinType
import javax.lang.model.element.Element

data class PrimitiveKType(
    val element: Element
) : KType(element) {

    override val adapterFieldName: String
        get() = ""

    fun getPrimitiveTypeNameForJsonReader(): String {
        val doubleType = setOf("Double", "Float")
        val simpleName = (element.asType().asTypeName() as ClassName).simpleName
        return if (simpleName in doubleType) {
            return "Double"
        } else {
            simpleName
        }
    }

    companion object {
        fun isPrimitive(element: Element): Boolean {
            val kotlinType = element.asType().asTypeName().kotlinType()
            if (kotlinType !is ClassName) {
                return false
            }
            return kotlinType.simpleName in setOf(
                "Int",
                "Long",
                "Boolean",
                "Double",
                "String",
                "Float"
            )
        }
    }
}