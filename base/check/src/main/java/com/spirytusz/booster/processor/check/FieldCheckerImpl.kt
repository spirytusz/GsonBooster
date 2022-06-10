package com.spirytusz.booster.processor.check

import com.spirytusz.booster.processor.base.check.FieldChecker
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.check.field.FieldMapTypeKeyGenericsChecker
import com.spirytusz.booster.processor.check.field.FieldNamingChecker
import com.spirytusz.booster.processor.check.field.FieldTypeVarianceChecker

internal class FieldCheckerImpl(private val messageLogger: MessageLogger) : FieldChecker {

    private val fieldNamingChecker by lazy { FieldNamingChecker(messageLogger) }

    private val fieldMapTypeKeyGenericsChecker by lazy {
        FieldMapTypeKeyGenericsChecker(messageLogger)
    }

    private val fieldTypeVarianceChecker by lazy { FieldTypeVarianceChecker(messageLogger) }
    override fun check(classScanner: ClassScanner, ktField: KtField) {
        fieldNamingChecker.check(classScanner, ktField)
        fieldMapTypeKeyGenericsChecker.check(classScanner, ktField)
        fieldTypeVarianceChecker.check(classScanner, ktField)
    }
}