package com.spirytusz.booster.processor.data.type

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.*
import com.spirytusz.booster.processor.const.TYPE_ADAPTER_NAME
import com.spirytusz.booster.processor.extensions.firstCharLowerCase

data class BackoffKType(
    val backoffTypeName: TypeName
) : KType(backoffTypeName) {

    override val adapterFieldName: String
        get() = _adapterFieldName

    private val _adapterFieldName: String by lazy {
        getAdapterFieldName(typeName)
    }

    override val jsonTokenName: String
        get() = JsonToken.BEGIN_OBJECT.name

    private fun getAdapterFieldName(typeName: TypeName): String {
        val adapterFieldName = getAdapterFieldNameRecursively(typeName).joinToString("")
        return "$adapterFieldName$TYPE_ADAPTER_NAME".firstCharLowerCase()
    }

    private fun getAdapterFieldNameRecursively(typeName: TypeName): List<String> {
        val names = mutableListOf<String>()
        when (typeName) {
            is ClassName -> {
                names.add(typeName.simpleName)
            }
            is ParameterizedTypeName -> {
                names.add(typeName.rawType.simpleName)
                typeName.typeArguments.forEach {
                    names.addAll(getAdapterFieldNameRecursively(it))
                }
            }
            is WildcardTypeName -> {
                // 协变逆变通配符
                val inTypeNames = typeName.inTypes.map {
                    getAdapterFieldNameRecursively(it).joinToString("")
                }
                val outTypeNames = typeName.outTypes.map {
                    getAdapterFieldNameRecursively(it).joinToString("")
                }
                names.addAll(inTypeNames)
                names.addAll(outTypeNames)
            }
            is Dynamic -> {
                // 动态类型，忽略
            }
            is LambdaTypeName -> {
                // lambda类型，忽略
            }
            is TypeVariableName -> {
                // 泛型名称
                names.add(typeName.name)
            }
        }
        return names
    }
}