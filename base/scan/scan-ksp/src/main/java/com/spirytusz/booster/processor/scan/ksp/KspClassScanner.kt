package com.spirytusz.booster.processor.scan.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.scan.ksp.impl.KspClassScannerFactory

class KspClassScanner(
    private val resolver: Resolver,
    private val ksClass: KSClassDeclaration,
    private val environment: SymbolProcessorEnvironment,
    private val logger: MessageLogger
) : ClassScanner {

    override val ktFields: List<KtField> by lazy {
        KspClassScannerFactory.create(environment, resolver, ksClass, logger).ktFields
    }
}