package com.spirytusz.booster.processor.scan.resolver

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSType
import com.spirytusz.booster.processor.data.JsonTokenName
import kotlin.reflect.KClass

class JsonTokenNameResolver(
    private val environment: SymbolProcessorEnvironment,
    private val resolver: Resolver
) {
    fun resolve(ksType: KSType): JsonTokenName {
        val resolved = when {
            ksType.isChildTypeOf(Int::class) -> JsonTokenName.INT
            ksType.isChildTypeOf(Long::class) -> JsonTokenName.LONG
            ksType.isChildTypeOf(Float::class) -> JsonTokenName.FLOAT
            ksType.isChildTypeOf(Double::class) -> JsonTokenName.DOUBLE
            ksType.isChildTypeOf(String()::class) -> JsonTokenName.STRING
            ksType.isChildTypeOf(Boolean::class) -> JsonTokenName.BOOLEAN
            ksType.isChildTypeOf(List::class) -> JsonTokenName.LIST
            ksType.isChildTypeOf(Set::class) -> JsonTokenName.SET
            else -> JsonTokenName.OBJECT
        }
        environment.logger.warn("resolve() >>> ksType=$ksType, resolved=${resolved.name}")
        return resolved
    }

    private fun KSType.isChildTypeOf(kClass: KClass<*>): Boolean {
        val type = getKSTypeFromName(kClass.qualifiedName.toString()) ?: return false
        return isAssignableFrom(type)
    }

    private fun getKSTypeFromName(canonicalName: String): KSType? {
        val declaration = resolver.getClassDeclarationByName(canonicalName)
        return declaration?.asType(typeArguments = listOf())
    }
}