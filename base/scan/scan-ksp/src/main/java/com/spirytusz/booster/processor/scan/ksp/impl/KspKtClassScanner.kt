package com.spirytusz.booster.processor.scan.ksp.impl

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.scan.ksp.data.KspKtField

class KspKtClassScanner(
    environment: SymbolProcessorEnvironment,
    resolver: Resolver,
    ksClass: KSClassDeclaration,
    logger: MessageLogger
) : KspAbstractClassScanner(environment, resolver, ksClass, logger) {
    override fun createKtFieldFromKSValueParameter(ksValueParameter: KSValueParameter): KspKtField {
        val initializer = if (ksValueParameter.hasDefault) {
            FieldInitializer.HAS_DEFAULT
        } else {
            FieldInitializer.NONE
        }
        val transient = ksValueParameter.annotations.any {
            it.shortName.asString() == Transient::class.simpleName
        }
        return KspKtField(
            keys = resolveKeys(
                ksValueParameter.name?.asString().toString(),
                ksValueParameter.annotations.toList()
            ),
            isFinal = !ksValueParameter.isVar,
            fieldName = ksValueParameter.name?.asString().toString(),
            ktType = createKtTypeFromKSType(ksValueParameter.type.resolve()),
            initializer = initializer,
            transient = transient,
            declarationScope = DeclarationScope.PRIMARY_CONSTRUCTOR,
            target = ksValueParameter
        )
    }

    override fun createKtFieldFromKSPropertyDeclaration(ksPropertyDeclaration: KSPropertyDeclaration): KspKtField {
        val initializer =
            if (ksPropertyDeclaration.hasBackingField && !ksPropertyDeclaration.isDelegated()) {
                FieldInitializer.HAS_DEFAULT
            } else {
                FieldInitializer.NONE
            }
        return KspKtField(
            keys = resolveKeys(
                ksPropertyDeclaration.simpleName.asString(),
                ksPropertyDeclaration.annotations.toList()
            ),
            isFinal = !ksPropertyDeclaration.isMutable,
            fieldName = ksPropertyDeclaration.simpleName.asString(),
            ktType = createKtTypeFromKSType(ksPropertyDeclaration.type.resolve()),
            initializer = initializer,
            transient = ksPropertyDeclaration.modifiers.contains(Modifier.JAVA_TRANSIENT),
            declarationScope = DeclarationScope.BODY,
            target = ksPropertyDeclaration
        )
    }
}