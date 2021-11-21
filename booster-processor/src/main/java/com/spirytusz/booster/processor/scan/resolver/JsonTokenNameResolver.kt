package com.spirytusz.booster.processor.scan.resolver

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.spirytusz.booster.processor.data.JsonTokenName
import com.spirytusz.booster.processor.extension.isChildTypeOf

class JsonTokenNameResolver(
    private val environment: SymbolProcessorEnvironment,
    private val resolver: Resolver
) {
    fun resolve(ksType: KSType): JsonTokenName {
        return when {
            ksType.isAssignableFrom(resolver.builtIns.intType) -> JsonTokenName.INT
            ksType.isAssignableFrom(resolver.builtIns.longType) -> JsonTokenName.LONG
            ksType.isAssignableFrom(resolver.builtIns.floatType) -> JsonTokenName.FLOAT
            ksType.isAssignableFrom(resolver.builtIns.doubleType) -> JsonTokenName.DOUBLE
            ksType.isAssignableFrom(resolver.builtIns.stringType) -> JsonTokenName.STRING
            ksType.isAssignableFrom(resolver.builtIns.booleanType) -> JsonTokenName.BOOLEAN
            ksType.isChildTypeOf<List<*>>(resolver) -> JsonTokenName.LIST
            ksType.isChildTypeOf<Set<*>>(resolver) -> JsonTokenName.SET
            ksType.isEnum() -> JsonTokenName.ENUM
            else -> JsonTokenName.OBJECT
        }
    }

    private fun KSType.isEnum(): Boolean {
        return (declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS
    }
}