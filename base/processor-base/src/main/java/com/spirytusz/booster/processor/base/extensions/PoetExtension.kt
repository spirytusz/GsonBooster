package com.spirytusz.booster.processor.base.extensions

import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe

fun KClass<*>.parameterizedBy(typeName: TypeName): ParameterizedTypeName {
    return with(ParameterizedTypeName) {
        asClassName().parameterizedBy(typeName)
    }
}

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

fun TypeName.javaType(): TypeName {
    return if (this is ParameterizedTypeName) {
        (rawType.javaType() as ClassName).parameterizedBy(
            *typeArguments.map { it.javaType() }.toTypedArray()
        )
    } else {
        val className =
            JavaToKotlinClassMap.INSTANCE.mapKotlinToJava(FqNameUnsafe(toString()))
                ?.asSingleFqName()
                ?.asString()

        return if (className == null) {
            this
        } else {
            ClassName.bestGuess(className)
        }
    }
}

fun KtType.asTypeName(): TypeName {
    val typeName = if (generics.isEmpty()) {
        ClassName.bestGuess(rawType).copy(nullable = nullable)
    } else {
        ClassName.bestGuess(rawType)
            .parameterizedBy(*generics.map { it.asTypeName() }.toTypedArray())
            .copy(nullable = nullable)
    }
    return when (this.variance) {
        KtVariance.IN -> WildcardTypeName.consumerOf(typeName)
        KtVariance.OUT -> WildcardTypeName.producerOf(typeName)
        else -> typeName
    }
}