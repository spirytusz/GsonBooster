package com.spirytusz.booster.processor.scan

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration

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