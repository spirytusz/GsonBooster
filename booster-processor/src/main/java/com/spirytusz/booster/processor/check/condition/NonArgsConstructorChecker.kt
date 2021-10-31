package com.spirytusz.booster.processor.check.condition

import com.spirytusz.booster.processor.check.api.AbstractClassPropertiesChecker
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class NonArgsConstructorChecker : AbstractClassPropertiesChecker() {
    override fun calculateInvalidProperties(classScanner: AbstractClassScanner): Set<PropertyDescriptor> {
        return classScanner.primaryConstructorProperties.filterNot { it.transient }
            .filterNot { it.hasDefault }.toSet()
    }

    override fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    ) {
        val className = classScanner.ksClass.qualifiedName?.asString().toString()
        val invalidPropertyNames = invalidProperties.map { it.fieldName }
        val msg = "$className without non-args constructor. properties: $invalidPropertyNames"
        throw WithoutNonArgsConstructorException(msg)
    }

    private class WithoutNonArgsConstructorException(msg: String) : IllegalArgumentException(msg)
}