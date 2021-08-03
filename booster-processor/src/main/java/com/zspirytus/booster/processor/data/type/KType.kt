package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.zspirytus.booster.processor.extensions.kotlinType
import com.zspirytus.booster.processor.helper.TypeHelper
import javax.lang.model.element.VariableElement

abstract class KType(variableElement: VariableElement) {

    val typeName: TypeName by lazy { variableElement.asType().asTypeName() }

    abstract val adapterFieldName: String

    companion object {

        fun makeKType(typeHelper: TypeHelper, variableElement: VariableElement): KType {
            return when {
                isPrimitive(variableElement) -> {
                    PrimitiveKType(variableElement)
                }
                typeHelper.isList(variableElement.asType()) -> {
                    val parameterizedTypeName =
                        variableElement.asType().asTypeName() as ParameterizedTypeName
                    val generic = parameterizedTypeName.typeArguments.first()
                    if (generic is ClassName) {
                        CollectionKType(
                            variableElement,
                            parameterizedTypeName.rawType,
                            generic
                        )
                    } else {
                        BackoffKType(variableElement)
                    }
                }
                variableElement.asType().asTypeName() is ClassName -> {
                    ObjectKType(variableElement)
                }
                else -> {
                    BackoffKType(variableElement)
                }
            }
        }

        private fun isPrimitive(variableElement: VariableElement): Boolean {
            val kotlinType = variableElement.asType().asTypeName().kotlinType()
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