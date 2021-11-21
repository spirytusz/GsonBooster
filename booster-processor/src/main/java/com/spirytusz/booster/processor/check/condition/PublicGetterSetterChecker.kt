package com.spirytusz.booster.processor.check.condition

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.spirytusz.booster.processor.check.api.AbstractClassPropertiesChecker
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.extension.error
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class PublicGetterSetterChecker(
    private val environment: SymbolProcessorEnvironment
) : AbstractClassPropertiesChecker() {

    companion object {
        private const val TAG = "PublicGetterSetterChecker"
    }

    override fun calculateInvalidProperties(classScanner: AbstractClassScanner): Set<PropertyDescriptor> {
        return classScanner.classProperties.filterNot { it.transient }.filterNot { it.mutable }
            .toSet()
    }

    override fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    ) {
        val invalidPropertyNames = invalidProperties.map { it.fieldName }
        val msg = "properties: $invalidPropertyNames without public getter setter."
        environment.logger.error(TAG, msg, classScanner.ksClass)
    }
}