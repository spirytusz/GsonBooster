package com.spirytusz.booster.processor.gen.extension

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName

@KotlinPoetKspPreview
fun KSClassDeclaration.asTypeName(): TypeName {
    val ksType = this.asType(typeArguments = listOf())
    return ksType.toTypeName()
}

