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

class KspJavaClassScanner(
    environment: SymbolProcessorEnvironment,
    resolver: Resolver,
    ksClass: KSClassDeclaration,
    logger: MessageLogger
) : KspAbstractClassScanner(environment, resolver, ksClass, logger) {
    override fun createKtFieldFromKSValueParameter(ksValueParameter: KSValueParameter): KspKtField {
        return KspKtField(
            keys = resolveKeys(ksValueParameter.annotations.toList()),
            isFinal = ksValueParameter.isVar,
            fieldName = ksValueParameter.name?.asString().toString(),
            ktType = createKtTypeFromKSType(ksValueParameter.type.resolve()),
            initializer = FieldInitializer.NONE,
            transient = false,
            declarationScope = DeclarationScope.PRIMARY_CONSTRUCTOR,
            target = ksValueParameter
        )
    }

    override fun createKtFieldFromKSPropertyDeclaration(ksPropertyDeclaration: KSPropertyDeclaration): KspKtField {
        return KspKtField(
            keys = resolveKeys(ksPropertyDeclaration.annotations.toList()),
            isFinal = ksPropertyDeclaration.isMutable,
            fieldName = ksPropertyDeclaration.simpleName.asString(),
            ktType = createKtTypeFromKSType(ksPropertyDeclaration.type.resolve()),
            initializer = FieldInitializer.HAS_DEFAULT,
            transient = ksPropertyDeclaration.modifiers.contains(Modifier.JAVA_TRANSIENT),
            declarationScope = DeclarationScope.BODY,
            target = ksPropertyDeclaration
        )
    }
}