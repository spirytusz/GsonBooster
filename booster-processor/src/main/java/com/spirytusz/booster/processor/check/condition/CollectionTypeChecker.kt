package com.spirytusz.booster.processor.check.condition

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.spirytusz.booster.processor.check.api.AbstractClassPropertiesChecker
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.extension.error
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class CollectionTypeChecker(private val environment: SymbolProcessorEnvironment) :
    AbstractClassPropertiesChecker() {

    companion object {
        private const val TAG = "CollectionTypeChecker"
    }

    override fun calculateInvalidProperties(classScanner: AbstractClassScanner): Set<PropertyDescriptor> {
        val expectCollectionType = setOf(
            List::class.qualifiedName, "kotlin.collections.MutableList",
            Set::class.qualifiedName, "kotlin.collections.MutableSet"
        )

        return classScanner.allProperties.filterNot {
            it.transient
        }.filter {
            it.type.isArray()
        }.filter {
            it.type.raw !in expectCollectionType
        }.toSet()
    }

    override fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    ) {
        invalidProperties.forEach {
            environment.logger.error(
                TAG,
                "property: ${it.fieldName} with unsupported collection type",
                it.origin
            )
        }
    }
}