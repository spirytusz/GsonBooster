package com.zspirytus.booster.processor.helper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

object TypeHelper {

    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types

    fun init(processingEnv: ProcessingEnvironment) {
        elementUtils = processingEnv.elementUtils
        typeUtils = processingEnv.typeUtils
    }

    fun isList(typeMirror: TypeMirror): Boolean {
        val javaListClassName = ClassName("java.util", "List")
        val kotlinListClassName = List::class.asTypeName()
        return isInterfaceOf(typeMirror, javaListClassName) || isInterfaceOf(
            typeMirror,
            kotlinListClassName
        )
    }

    fun isSet(typeMirror: TypeMirror): Boolean {
        val javaSetClassName = ClassName("java.util", "Set")
        val kotlinSetClassName = Set::class.asTypeName()
        return isInterfaceOf(typeMirror, javaSetClassName) || isInterfaceOf(
            typeMirror,
            kotlinSetClassName
        )
    }

    fun getElementFromClassName(className: ClassName): Element {
        return elementUtils.getTypeElement(className.canonicalName)
    }

    fun isInterfaceOf(typeMirror: TypeMirror, interfaceName: TypeName): Boolean {
        val typeName = typeMirror.asTypeName()
        if (typeName !is ParameterizedTypeName) {
            return false
        }
        val rawType = typeName.rawType
        if (rawType == interfaceName) {
            return true
        } else {
            typeUtils.directSupertypes(typeMirror).forEach { mirror ->
                if (isInterfaceOf(mirror, interfaceName)) {
                    return true
                }
            }
            return false
        }
    }
}