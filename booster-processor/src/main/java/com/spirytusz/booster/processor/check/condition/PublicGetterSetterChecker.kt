package com.spirytusz.booster.processor.check.condition

import com.spirytusz.booster.processor.check.api.AbstractClassPropertiesChecker
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class PublicGetterSetterChecker : AbstractClassPropertiesChecker() {
    override fun calculateInvalidProperties(classScanner: AbstractClassScanner): Set<PropertyDescriptor> {
        return classScanner.classProperties.filterNot { it.transient }.filterNot { it.mutable }
            .toSet()
    }

    override fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    ) {
        val className = classScanner.ksClass.qualifiedName?.asString().toString()
        val invalidPropertyNames = invalidProperties.map { it.fieldName }
        val msg = "$className properties: $invalidPropertyNames without public getter setter."
        throw WithoutPublicGetterSetterException(msg)
    }

    private class WithoutPublicGetterSetterException(msg: String) : IllegalArgumentException(msg)
}