package com.spirytusz.booster.processor.check

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.spirytusz.booster.processor.check.condition.CollectionTypeChecker
import com.spirytusz.booster.processor.check.condition.KotlinKeywordPropertyChecker
import com.spirytusz.booster.processor.check.condition.NonArgsConstructorChecker
import com.spirytusz.booster.processor.check.condition.PublicGetterSetterChecker
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class CodeCheckProcessor(
    private val environment: SymbolProcessorEnvironment
) {

    private val collectionTypeChecker by lazy {
        CollectionTypeChecker(environment)
    }

    private val kotlinKeywordPropertyChecker by lazy {
        KotlinKeywordPropertyChecker(environment)
    }

    private val nonArgsConstructorChecker by lazy {
        NonArgsConstructorChecker(environment)
    }

    private val publicGetterSetterChecker by lazy {
        PublicGetterSetterChecker(environment)
    }

    fun process(classScanners: Set<AbstractClassScanner>) {
        classScanners.forEach { classScanner ->
            collectionTypeChecker.check(classScanner)
            kotlinKeywordPropertyChecker.check(classScanner)
            nonArgsConstructorChecker.check(classScanner)
            publicGetterSetterChecker.check(classScanner)
        }
    }
}