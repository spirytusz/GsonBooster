package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.VariableElement

data class PrimitiveKType(
    val element: VariableElement
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
}