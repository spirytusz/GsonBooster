package com.spirytusz.booster.processor.scan.ksp.impl

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Origin
import com.spirytusz.booster.processor.base.log.MessageLogger

object KspClassScannerFactory {

    fun create(
        environment: SymbolProcessorEnvironment,
        resolver: Resolver,
        ksClass: KSClassDeclaration,
        logger: MessageLogger
    ): KspAbstractClassScanner {
        val containingFile = ksClass.containingFile ?: run {
            logger.error("invalid class ${ksClass.qualifiedName?.asString()}", ksClass)
            throw IllegalArgumentException("invalid class ${ksClass.qualifiedName?.asString()}")
        }
        return when (containingFile.origin) {
            Origin.JAVA -> KspJavaClassScanner(environment, resolver, ksClass, logger)
            Origin.KOTLIN -> KspKtClassScanner(environment, resolver, ksClass, logger)
            else -> {
                logger.error("Unexpected origin", ksClass)
                throw IllegalArgumentException("Unexpected origin")
            }
        }
    }
}