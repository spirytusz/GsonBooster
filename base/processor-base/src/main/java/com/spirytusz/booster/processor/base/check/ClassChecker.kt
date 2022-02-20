package com.spirytusz.booster.processor.base.check

import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner

interface ClassChecker {

    fun check(classScanner: ClassScanner)

    interface Factory {

        fun create(logger: MessageLogger): ClassChecker
    }
}