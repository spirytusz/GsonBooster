package com.spirytusz.booster.processor.check.api

import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

interface ICodeChecker {

    fun check(classScanner: AbstractClassScanner)
}