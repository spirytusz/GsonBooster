package com.spirytusz.booster.processor.scan

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration

object ClassScannerFactory {

    fun createClassScanner(
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
            KotlinClassScanner(environment, ksClassDeclaration)
        } else {
            JavaClassScanner(environment, ksClassDeclaration)
        }
    }
}