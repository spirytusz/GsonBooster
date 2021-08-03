package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.zspirytus.booster.processor.extensions.kotlinType
import com.zspirytus.booster.processor.helper.TypeHelper
import javax.lang.model.element.Element

abstract class KType(element: Element) {

    val typeName: TypeName by lazy { element.asType().asTypeName() }

    abstract val adapterFieldName: String

    companion object {

        fun makeKType(typeHelper: TypeHelper, element: Element): KType {
            return when {
                isPrimitive(element) -> {
                    PrimitiveKType(element)
                }
                typeHelper.isList(element.asType()) -> {
                    val parameterizedTypeName =
                        element.asType().asTypeName() as ParameterizedTypeName
                    val generic = parameterizedTypeName.typeArguments.first()
                    if (generic is ClassName) {
                        CollectionKType(
                            element,
                            parameterizedTypeName.rawType,
                            generic
                        )
                    } else {
                        BackoffKType(element)
                    }
                }
                element.asType().asTypeName() is ClassName -> {
                    ObjectKType(element)
                }
                else -> {
                    BackoffKType(element)
                }
            }
        }

        private fun isPrimitive(element: Element): Boolean {
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