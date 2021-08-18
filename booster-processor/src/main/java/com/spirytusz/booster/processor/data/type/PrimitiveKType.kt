package com.spirytusz.booster.processor.data.type

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.extensions.kotlinType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

/**
 * 代表一个原始类型
 *
 * @param className 类型名
 */
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

    fun isFloat(): Boolean = className.simpleName == "Float"

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