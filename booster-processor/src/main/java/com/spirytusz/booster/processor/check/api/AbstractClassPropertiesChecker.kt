package com.spirytusz.booster.processor.check.api

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

abstract class AbstractClassPropertiesChecker {

    fun check(classScanner: AbstractClassScanner) {
        val invalidProperties = calculateInvalidProperties(classScanner)
        if (invalidProperties.isEmpty()) {
            onPass(classScanner)
            return
        }

        onError(classScanner, invalidProperties)
    }

    /**
     * invalid Properties ...
     */
    protected abstract fun calculateInvalidProperties(
        classScanner: AbstractClassScanner
    ): Set<PropertyDescriptor>

    /**
     * pass
     */
    protected open fun onPass(classScanner: AbstractClassScanner) {}

    /**
     * error and throw [IllegalArgumentException]
     */
    @Throws(IllegalArgumentException::class)
    protected abstract fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    )
}