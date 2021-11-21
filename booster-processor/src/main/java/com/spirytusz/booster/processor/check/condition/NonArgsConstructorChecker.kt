package com.spirytusz.booster.processor.check.condition

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.spirytusz.booster.processor.check.api.AbstractClassPropertiesChecker
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.extension.error
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class NonArgsConstructorChecker(
    private val environment: SymbolProcessorEnvironment
) : AbstractClassPropertiesChecker() {

    companion object {
        private const val TAG = "NonArgsConstructorChecker"
    }

    override fun calculateInvalidProperties(classScanner: AbstractClassScanner): Set<PropertyDescriptor> {
        return classScanner.primaryConstructorProperties.filterNot { it.transient }
            .filterNot { it.hasDefault }.toSet()
    }

    override fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    ) {
        val invalidPropertyNames = invalidProperties.map { it.fieldName }
        val msg = "without non-args constructor. properties: $invalidPropertyNames"
        environment.logger.error(TAG, msg, classScanner.ksClass)
    }
}