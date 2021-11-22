package com.spirytusz.booster.processor.check.condition

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.spirytusz.booster.processor.check.api.AbstractClassChecker
import com.spirytusz.booster.processor.extension.error
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class ClassGenericsChecker(
    private val environment: SymbolProcessorEnvironment
) : AbstractClassChecker() {

    companion object {
        private const val TAG = "ClassGenericsChecker"
    }

    override fun isValidClass(classScanner: AbstractClassScanner): Boolean {
        return classScanner.ksClass.typeParameters.isEmpty()
    }

    override fun onError(classScanner: AbstractClassScanner) {
        environment.logger.error(
            TAG,
            "class: ${classScanner.ksClass.simpleName.asString()} with type variance",
            classScanner.ksClass
        )
    }
}