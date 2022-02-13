package com.spirytusz.booster.processor.base.check

import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.scan.ClassScanner

interface FieldChecker {

    fun check(classScanner: ClassScanner, ktField: KtField)
}