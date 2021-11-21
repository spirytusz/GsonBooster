package com.spirytusz.booster.processor.extension

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.spirytusz.booster.processor.data.TypeDescriptor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName

fun KSClassDeclaration.asTypeName(): TypeName {
    val ksType = this.asType(typeArguments = listOf())
    return ksType.toTypeName()
}

fun TypeDescriptor.asTypeName(): TypeName {
    val typeName = origin.toTypeName()
    return if (typeName is ClassName && typeArguments.isNotEmpty()) {
        typeName.parameterizedBy(typeArguments.map { it.asTypeName() })
    } else {
        typeName
    }
}

