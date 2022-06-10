package com.spirytusz.booster.processor.check.field

import com.spirytusz.booster.processor.base.check.FieldChecker
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner

internal class FieldTypeVarianceChecker(private val messageLogger: MessageLogger) : FieldChecker {
    override fun check(classScanner: ClassScanner, ktField: KtField) {
        val variantTypes = ktField.ktType.dfs { variance != KtVariance.INVARIANT }
        if (variantTypes.isEmpty()) {
            return
        }
        messageLogger.warn("not recommend variant type", ktField)
    }
}