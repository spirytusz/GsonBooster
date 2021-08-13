package com.spirytusz.booster.processor.data.type

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.spirytusz.booster.processor.extensions.kotlinType

data class PrimitiveKType(
    val className: ClassName
) : KType(className) {

    override val adapterFieldName: String
        get() = ""

    override val jsonTokenName: String
        get() = _jsonTokenName

    private val _jsonTokenName by lazy {
        getJsonTokenNameInternal()
    }

    fun getPrimitiveTypeNameForJsonReader(): String {
        return getPrimitiveTypeNameForJsonReader(className)
    }

    private fun getJsonTokenNameInternal(): String {
        return when (getPrimitiveTypeNameForJsonReader()) {
            "Int", "Long", "Double" -> JsonToken.NUMBER.name
            "Boolean" -> JsonToken.BOOLEAN.name
            "String" -> JsonToken.STRING.name
            else -> throw IllegalStateException("unknown primitive type $className to cast JsonToken")
        }
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