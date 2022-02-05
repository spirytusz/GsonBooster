package com.spirytusz.booster.processor.scan.kapt.base

import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.extensions.javaType
import com.squareup.kotlinpoet.*
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

abstract class AbstractKmTypeResolver(
    processingEnvironment: ProcessingEnvironment,
    protected val kmType: KmType
) {

    abstract val resolvedKtType: KtType

    private val elementUtils = processingEnvironment.elementUtils
    private val typeUtils = processingEnvironment.typeUtils

    protected fun isPrimitive(kmType: KmType): Boolean {
        val kotlinType = kmType.rawAsJavaClassName()
        return kotlinType.simpleName in setOf(
            "Integer",
            "Long",
            "Boolean",
            "Double",
            "String",
            "Float"
        )
    }

    protected fun isKotlinList(kmType: KmType): Boolean {
        return isList(kmType) && (kmType.classifier as KmClassifier.Class).name.startsWith("kotlin")
    }

    protected fun isJavaList(kmType: KmType): Boolean {
        return isList(kmType) && !(kmType.classifier as KmClassifier.Class).name.startsWith("kotlin")
    }

    private fun isList(kmType: KmType): Boolean {
        return isInterfaceOf(kmType.rawAsJavaClassName(), List::class.java.asClassName())
    }

    protected fun isKotlinSet(kmType: KmType): Boolean {
        return isSet(kmType) && (kmType.classifier as KmClassifier.Class).name.startsWith("kotlin")
    }

    protected fun isJavaSet(kmType: KmType): Boolean {
        return isSet(kmType) && !(kmType.classifier as KmClassifier.Class).name.startsWith("kotlin")
    }

    private fun isSet(kmType: KmType): Boolean {
        return isInterfaceOf(kmType.rawAsJavaClassName(), Set::class.java.asClassName())
    }

    protected fun isKotlinMap(kmType: KmType): Boolean {
        return isMap(kmType) && (kmType.classifier as KmClassifier.Class).name.startsWith("kotlin")
    }

    protected fun isJavaMap(kmType: KmType): Boolean {
        return isMap(kmType) && !(kmType.classifier as KmClassifier.Class).name.startsWith("kotlin")
    }

    private fun isMap(kmType: KmType): Boolean {
        return isInterfaceOf(kmType.rawAsJavaClassName(), Map::class.java.asClassName())
    }

    protected fun isEnum(kmType: KmType): Boolean {
        val typeName = kmType.rawAsJavaClassName()
        return getElementFromClassName(typeName).kind == ElementKind.ENUM
    }

    private fun isInterfaceOf(typeName: TypeName, interfaceName: ClassName): Boolean {
        return when (typeName) {
            is ClassName -> {
                if (typeName == interfaceName) {
                    true
                } else {
                    val element = getElementFromClassName(typeName)
                    val mirror = element.asType()
                    typeUtils.directSupertypes(mirror).any { superTypeMirror ->
                        isInterfaceOf(superTypeMirror.asTypeName(), interfaceName)
                    }
                }
            }
            is ParameterizedTypeName -> {
                isInterfaceOf(typeName.rawType, interfaceName)
            }
            is WildcardTypeName -> {
                // TODO inTypes or outTypes?
                false
            }
            else -> false
        }
    }

    private fun KmType.rawAsJavaClassName(): ClassName {
        val name = (classifier as KmClassifier.Class).name.replace("/", ".")
        return ClassName.bestGuess(name).javaType() as ClassName
    }

    private fun getElementFromClassName(className: ClassName): Element {
        return elementUtils.getTypeElement(className.canonicalName)
    }
}