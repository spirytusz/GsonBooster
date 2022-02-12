package com.spirytusz.booster.processor.scan.ksp.extensions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias

fun KSType.isChildTypeOf(fqName: String): Boolean {
    var declaration = declaration
    while (declaration is KSTypeAlias) {
        declaration = declaration.type.resolve().declaration
    }

    if (declaration.qualifiedName?.asString() == fqName) {
        return true
    }

    return (declaration.findActualDeclaration() as KSClassDeclaration).superTypes.any {
        it.resolve().isChildTypeOf(fqName)
    }
}

fun KSDeclaration.findActualDeclaration(): KSDeclaration {
    return if (this is KSTypeAlias) {
        this.type.resolve().declaration.findActualDeclaration()
    } else {
        this
    }
}