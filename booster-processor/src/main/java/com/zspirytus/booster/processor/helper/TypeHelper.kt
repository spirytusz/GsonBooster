package com.zspirytus.booster.processor.helper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

object TypeHelper {

    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    fun init(processingEnv: ProcessingEnvironment) {
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
    }

    fun isList(typeName: TypeName): Boolean {
        return when (typeName) {
            is ClassName -> {
                val javaListClassName = ClassName("java.util", "List")
                val kotlinListClassName = List::class.asTypeName()
                isInterfaceOf(typeName, javaListClassName) ||
                        isInterfaceOf(typeName, kotlinListClassName)
            }
            is ParameterizedTypeName -> {
                isList(typeName.rawType)
            }
            else -> false
        }
    }

    fun isSet(typeName: TypeName): Boolean {
        return when (typeName) {
            is ClassName -> {
                val javaSetClassName = ClassName("java.util", "Set")
                val kotlinSetClassName = Set::class.asTypeName()
                isInterfaceOf(typeName, javaSetClassName) ||
                        isInterfaceOf(typeName, kotlinSetClassName)
            }
            is ParameterizedTypeName -> {
                isSet(typeName.rawType)
            }
            else -> false
        }
    }

    fun getElementFromClassName(className: ClassName): Element {
        return elementUtils.getTypeElement(className.canonicalName)
    }

    fun isInterfaceOf(className: ClassName, interfaceName: TypeName): Boolean {
        if (className == interfaceName) {
            return true
        } else {
            val element = getElementFromClassName(className)
            val mirror = element.asType()
            typeUtils.directSupertypes(mirror).forEach { superTypeMirror ->
                val typeName = superTypeMirror.asTypeName()
                if (typeName is ClassName && isInterfaceOf(typeName, interfaceName)) {
                    return true
                }
            }
            return false
        }
    }
}