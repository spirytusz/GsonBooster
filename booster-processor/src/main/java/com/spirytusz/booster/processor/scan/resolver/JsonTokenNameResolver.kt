package com.spirytusz.booster.processor.scan.resolver

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.spirytusz.booster.processor.data.JsonTokenName
import kotlin.reflect.KClass

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
            ksType.isChildTypeOf(List::class) -> JsonTokenName.LIST
            ksType.isChildTypeOf(Set::class) -> JsonTokenName.SET
            ksType.isEnum() -> JsonTokenName.ENUM
            else -> JsonTokenName.OBJECT
        }
    }

    private fun KSType.isChildTypeOf(kClass: KClass<*>): Boolean {
        this.declaration
        val type = getKSTypeFromName(kClass.qualifiedName.toString(), arguments) ?: return false
        return isAssignableFrom(type)
    }

    private fun getKSTypeFromName(canonicalName: String, arguments: List<KSTypeArgument>): KSType? {
        val declaration = resolver.getClassDeclarationByName(canonicalName)
        return declaration?.asType(arguments)
    }

    private fun KSType.isEnum(): Boolean {
        return (declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS
    }
}