package com.spirytusz.booster.processor.check.condition

import com.spirytusz.booster.processor.check.api.AbstractClassPropertiesChecker
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class CollectionTypeChecker : AbstractClassPropertiesChecker() {

    override fun calculateInvalidProperties(classScanner: AbstractClassScanner): Set<PropertyDescriptor> {
        val expectCollectionType = setOf(
            List::class.qualifiedName, MutableList::class.qualifiedName,
            Set::class.qualifiedName, MutableSet::class.qualifiedName
        )
        return classScanner.allProperties.filter {
            it.type.isArray()
        }.filterNot {
            it.transient
        }.filter {
            it.type.raw in expectCollectionType
        }.toSet()
    }

    override fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    ) {
        val className = classScanner.ksClass.qualifiedName?.asString().toString()
        val invalidPropertyNames = invalidProperties.map { it.fieldName }
        val msg = "$className properties: $invalidPropertyNames with invalid collection type"

        throw InvalidCollectionTypeException(msg)
    }

    private class InvalidCollectionTypeException(msg: String) : IllegalArgumentException(msg)
}