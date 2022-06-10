package com.spirytusz.booster.processor.base.extensions

import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe

private val javaUnBoxedPrimitiveTypeNames by lazy {
    listOf(
        Int::class.java.canonicalName,
        Long::class.java.canonicalName,
        Float::class.java.canonicalName,
        Double::class.java.canonicalName,
        String::class.java.canonicalName,
        Boolean::class.java.canonicalName
    )
}

private val javaBoxedPrimitiveTypeNames by lazy {
    listOf(
        java.lang.Integer::class.java.canonicalName,
        java.lang.Long::class.java.canonicalName,
        java.lang.Float::class.java.canonicalName,
        java.lang.Double::class.java.canonicalName,
        java.lang.String::class.java.canonicalName,
        java.lang.Boolean::class.java.canonicalName
    )
}

private val kotlinPrimitiveTypeNames by lazy {
    listOf(
        Int::class.qualifiedName.toString(),
        Long::class.qualifiedName.toString(),
        Float::class.qualifiedName.toString(),
        Double::class.qualifiedName.toString(),
        String::class.qualifiedName.toString(),
        Boolean::class.qualifiedName.toString()
    )
}

private val primitiveTypeNames by lazy {
    javaUnBoxedPrimitiveTypeNames + javaBoxedPrimitiveTypeNames + kotlinPrimitiveTypeNames
}

fun KClass<*>.parameterizedBy(typeName: TypeName): ParameterizedTypeName {
    return with(ParameterizedTypeName) {
        asClassName().parameterizedBy(typeName)
    }
}

fun TypeName.kotlinPrimitiveType(): TypeName {
    return transform { canonicalName ->
        if (canonicalName !in primitiveTypeNames) {
            return@transform canonicalName
        }
        JavaToKotlinClassMap.INSTANCE
            .mapJavaToKotlin(FqName(canonicalName))
            ?.asSingleFqName()
            ?.asString() ?: canonicalName
    }
}

fun TypeName.kotlinType(): TypeName {
    return transform { canonicalName ->
        JavaToKotlinClassMap.INSTANCE
            .mapJavaToKotlin(FqName(canonicalName))
            ?.asSingleFqName()
            ?.asString() ?: canonicalName
    }
}

fun TypeName.javaType(): TypeName {
    return transform { canonicalName ->
        JavaToKotlinClassMap.INSTANCE
            .mapKotlinToJava(FqNameUnsafe(canonicalName))
            ?.asSingleFqName()
            ?.asString() ?: canonicalName
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T : TypeName> T.transform(transformer: (String) -> String): T {
    return when (this) {
        is ClassName -> ClassName.bestGuess(transformer(canonicalName))
        is ParameterizedTypeName -> {
            val arguments = typeArguments.map { it.transform(transformer) }
            rawType.transform(transformer).parameterizedBy(arguments)
        }
        is WildcardTypeName -> {
            if (inTypes.isNotEmpty()) {
                WildcardTypeName.consumerOf(inTypes.first().transform(transformer))
            } else {
                WildcardTypeName.producerOf(outTypes.first().transform(transformer))
            }
        }
        is TypeVariableName -> TypeVariableName.invoke(transformer(name), bounds, variance)
        else -> throw IllegalArgumentException("Unsupported TypeName $this")
    }.copy(nullable = isNullable) as T
}

fun KtType.asTypeName(
    ignoreVariance: Boolean = false,
    ignoreNullability: Boolean = false
): TypeName {
    var typeName = if (generics.isEmpty()) {
        ClassName.bestGuess(rawType)
    } else {
        ClassName.bestGuess(rawType)
            .parameterizedBy(*generics.map { it.asTypeName() }.toTypedArray())
    }
    if (!ignoreNullability) {
        typeName = typeName.copy(nullable = nullable)
    }
    if (!ignoreVariance) {
        typeName = when (this.variance) {
            KtVariance.IN -> WildcardTypeName.consumerOf(typeName)
            KtVariance.OUT -> WildcardTypeName.producerOf(typeName)
            else -> typeName
        }
    }
    return typeName
}