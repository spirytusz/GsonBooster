package com.spirytusz.booster.processor.check.api

import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

abstract class AbstractClassChecker : ICodeChecker {

    protected abstract fun isValidClass(classScanner: AbstractClassScanner): Boolean

    protected open fun onPass(classScanner: AbstractClassScanner) {}

    protected abstract fun onError(classScanner: AbstractClassScanner)

    final override fun check(classScanner: AbstractClassScanner) {
        if (isValidClass(classScanner)) {
            onPass(classScanner)
        } else {
            onError(classScanner)
        }
    }
}