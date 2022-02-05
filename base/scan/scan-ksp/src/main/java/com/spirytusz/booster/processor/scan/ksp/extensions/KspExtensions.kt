package com.spirytusz.booster.processor.scan.ksp.extensions

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument

inline fun <reified T> KSType.isChildTypeOf(resolver: Resolver): Boolean {
    return isChildTypeOf(resolver, T::class.qualifiedName!!) // kotlin
            || isChildTypeOf(resolver, T::class.java.canonicalName) // java
}

fun KSType.isChildTypeOf(resolver: Resolver, fqName: String): Boolean {
    val fqNameKSType = resolver.getClassDeclarationByName(fqName)?.asType(arguments) ?: return false
    val declaration = this.declaration
    val actualType = if (declaration is KSTypeAlias) {
        declaration.findActualType(arguments)
    } else {
        this
    }
    val actualTypeDeclaration = actualType.declaration as KSClassDeclaration
    if (actualType.isAssignableFrom(fqNameKSType)) {
        return true
    }
    return actualTypeDeclaration.superTypes.any {
        it.resolve().isChildTypeOf(resolver, fqName)
    }
}

fun KSTypeAlias.findActualType(arguments: List<KSTypeArgument>): KSType {
    val resolvedType = this.type.resolve().declaration
    return if (resolvedType is KSTypeAlias) {
        resolvedType.findActualType(arguments)
    } else {
        val sourceDeclaration = resolvedType as KSClassDeclaration
        sourceDeclaration.asType(arguments)
    }
}