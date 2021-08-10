package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.zspirytus.booster.processor.helper.TypeHelper

abstract class KType(val typeName: TypeName) {

    abstract val adapterFieldName: String

    abstract val jsonTokenName: String

    companion object {

        fun makeKTypeByTypeName(typeName: TypeName): KType {
            return when {
                PrimitiveKType.isPrimitive(typeName) -> {
                    // 原始类型
                    PrimitiveKType(typeName as ClassName)
                }
                TypeHelper.isList(typeName) || TypeHelper.isSet(typeName) -> {
                    // 集合类型
                    val parameterizedTypeName = typeName as ParameterizedTypeName
                    val generic = parameterizedTypeName.typeArguments.first()
                    if (generic is ClassName) {
                        CollectionKType(
                            parameterizedTypeName,
                            parameterizedTypeName.rawType,
                            generic
                        )
                    } else {
                        BackoffKType(typeName)
                    }
                }
                TypeHelper.isEnum(typeName) && typeName is ClassName -> {
                    // 不带泛型的枚举类型
                    EnumKType(typeName)
                }
                typeName is ClassName -> {
                    // 不带泛型的Class<*>类型
                    ObjectKType(typeName)
                }
                else -> {
                    // 退避类型，这种类型托管给gson自带的TypeAdapter解析
                    BackoffKType(typeName)
                }
            }
        }
    }
}