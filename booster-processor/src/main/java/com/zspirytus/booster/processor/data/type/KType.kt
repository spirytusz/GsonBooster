package com.zspirytus.booster.processor.data.type

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.zspirytus.booster.processor.helper.TypeHelper
import javax.lang.model.element.Element

abstract class KType(element: Element) {

    val typeName: TypeName by lazy { element.asType().asTypeName() }

    abstract val adapterFieldName: String

    companion object {

        fun makeKTypeByElement(element: Element): KType {
            return when {
                PrimitiveKType.isPrimitive(element) -> {
                    PrimitiveKType(element)
                }
                TypeHelper.isList(element.asType()) || TypeHelper.isSet(element.asType()) -> {
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

        fun makeKTypeByClassName(className: ClassName): KType {
            return makeKTypeByElement(TypeHelper.getElementFromClassName(className))
        }
    }
}