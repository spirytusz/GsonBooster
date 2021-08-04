package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.zspirytus.booster.processor.helper.TypeHelper

abstract class KType(val typeName: TypeName) {

    abstract val adapterFieldName: String

    companion object {

        fun makeKTypeByTypeName(typeName: TypeName): KType {
            return when {
                PrimitiveKType.isPrimitive(typeName) -> {
                    PrimitiveKType(typeName as ClassName)
                }
                TypeHelper.isList(typeName) || TypeHelper.isSet(typeName) -> {
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
                typeName is ClassName -> {
                    ObjectKType(typeName)
                }
                else -> {
                    BackoffKType(typeName)
                }
            }
        }
    }
}