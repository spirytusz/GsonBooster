package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.zspirytus.booster.processor.extensions.kotlinType

data class PrimitiveKType(
    val className: ClassName
) : KType(className) {

    override val adapterFieldName: String
        get() = ""

    fun getPrimitiveTypeNameForJsonReader(): String {
        return getPrimitiveTypeNameForJsonReader(className)
    }

    companion object {
        fun isPrimitive(typeName: TypeName): Boolean {
            val kotlinType = typeName.kotlinType()
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

        fun getPrimitiveTypeNameForJsonReader(className: ClassName): String {
            val doubleType = setOf("Double", "Float")
            val simpleName = (className.kotlinType() as ClassName).simpleName
            return if (simpleName in doubleType) {
                return "Double"
            } else {
                simpleName
            }
        }
    }
}