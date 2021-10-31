package com.spirytusz.booster.processor.scan.impl

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

/**
 * Java类扫描器
 */
class JavaClassScanner(
    resolver: Resolver,
    environment: SymbolProcessorEnvironment,
    ksClass: KSClassDeclaration
) : AbstractClassScanner(environment, resolver, ksClass) {

    override fun createPropertyDescriptorFromKSValueParameter(
        ksValueParameter: KSValueParameter
    ): PropertyDescriptor {
        return PropertyDescriptor(
            keys = resolveKeys(ksValueParameter.annotations.toList()),
            mutable = ksValueParameter.isVar,
            fieldName = ksValueParameter.name?.asString().toString(),
            type = createTypeDescriptorFromKSType(ksValueParameter.type.resolve()),
            hasDefault = ksValueParameter.hasDefault,
            transient = false
        )
    }

    override fun createPropertyDescriptorFromKSPropertyDeclaration(
        ksPropertyDeclaration: KSPropertyDeclaration
    ): PropertyDescriptor {
        return PropertyDescriptor(
            keys = resolveKeys(ksPropertyDeclaration.annotations.toList()),
            mutable = ksPropertyDeclaration.isMutable,
            fieldName = ksPropertyDeclaration.simpleName.asString(),
            type = createTypeDescriptorFromKSType(ksPropertyDeclaration.type.resolve()),
            hasDefault = ksPropertyDeclaration.hasBackingField,
            transient = ksPropertyDeclaration.modifiers.contains(Modifier.JAVA_TRANSIENT)
        )
    }
}