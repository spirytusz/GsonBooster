package com.spirytusz.booster.processor.check

import com.spirytusz.booster.processor.base.check.ClassChecker
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.check.clazz.ClassGenericsChecker
import com.spirytusz.booster.processor.check.clazz.InvisibleGetterSetterFieldChecker
import com.spirytusz.booster.processor.check.clazz.NoArgsConstructorChecker

internal class ClassCheckerImpl(private val messageLogger: MessageLogger) : ClassChecker {

    private val classGenericsChecker by lazy { ClassGenericsChecker(messageLogger) }

    private val noArgsConstructorChecker by lazy { NoArgsConstructorChecker(messageLogger) }

    private val invisibleGetterSetterFieldChecker by lazy {
        InvisibleGetterSetterFieldChecker(messageLogger)
    }

    private val fieldChecker by lazy { FieldCheckerImpl(messageLogger) }

    override fun check(classScanner: ClassScanner) {
        classGenericsChecker.check(classScanner)
        invisibleGetterSetterFieldChecker.check(classScanner)
        noArgsConstructorChecker.check(classScanner)

        classScanner.ktFields.forEach {
            fieldChecker.check(classScanner, it)
        }
    }
}