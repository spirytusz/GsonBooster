package com.spirytusz.booster.processor.check.clazz

import com.spirytusz.booster.processor.base.check.ClassChecker
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner

internal class ClassGenericsChecker(private val messageLogger: MessageLogger) : ClassChecker {

    override fun check(classScanner: ClassScanner) {
        val classKtType = classScanner.classKtType
        if (classKtType.generics.isEmpty()) {
            return
        }
        messageLogger.error("class with generics", classKtType)
        throw ClassWithGenericsException("class with generics [${classKtType.rawType}]")
    }

    private class ClassWithGenericsException(msg: String) : Exception(msg)
}