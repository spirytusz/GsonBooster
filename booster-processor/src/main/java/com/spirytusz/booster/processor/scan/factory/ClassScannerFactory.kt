package com.spirytusz.booster.processor.scan.factory

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.spirytusz.booster.processor.scan.impl.JavaClassScanner
import com.spirytusz.booster.processor.scan.impl.KotlinClassScanner

object ClassScannerFactory {

    fun createClassScanner(
        resolver: Resolver,
        environment: SymbolProcessorEnvironment,
        ksClassDeclaration: KSClassDeclaration
    ): AbstractClassScanner {
        val containingFile = ksClassDeclaration.containingFile
        if (containingFile == null) {
            environment.logger.warn(
                "createClassScanner() >>> " +
                        "[${ksClassDeclaration.simpleName.asString()}] " +
                        "null"
            )
        }
        containingFile!!
        return if (containingFile.fileName.endsWith(".kt")) {
            KotlinClassScanner(resolver, environment, ksClassDeclaration)
        } else {
            JavaClassScanner(resolver, environment, ksClassDeclaration)
        }
    }
}