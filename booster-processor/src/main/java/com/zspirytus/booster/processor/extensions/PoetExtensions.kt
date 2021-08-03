package com.zspirytus.booster.processor.extensions

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

fun KClass<*>.parameterizedBy(typeElement: TypeElement): ParameterizedTypeName {
    return parameterizedBy(typeElement.asType().asTypeName())
}

fun KClass<*>.parameterizedBy(typeName: TypeName): ParameterizedTypeName {
    return with(ParameterizedTypeName) {
        asClassName().parameterizedBy(typeName)
    }
}

fun ClassName.generateTypeAdapterName(): String =
    "${simpleNames.joinToString(separator = "_")}TypeAdapter"

fun TypeName.asNullable() = copy(nullable = true)

fun TypeName.kotlinType(): TypeName {
    return if (this is ParameterizedTypeName) {
        (rawType.kotlinType() as ClassName).parameterizedBy(
            *typeArguments.map { it.kotlinType() }.toTypedArray()
        )
    } else {
        val className =
            JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()
                ?.asString()

        return if (className == null) {
            this
        } else {
            ClassName.bestGuess(className)
        }
    }
}