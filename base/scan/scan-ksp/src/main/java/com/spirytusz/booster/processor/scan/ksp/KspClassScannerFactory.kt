package com.spirytusz.booster.processor.scan.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Origin
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.scan.ksp.impl.KspJavaClassScanner
import com.spirytusz.booster.processor.scan.ksp.impl.KspKtClassScanner

object KspClassScannerFactory {

    fun create(
        environment: SymbolProcessorEnvironment,
        resolver: Resolver,
        ksClass: KSClassDeclaration,
        logger: MessageLogger
    ): ClassScanner {
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