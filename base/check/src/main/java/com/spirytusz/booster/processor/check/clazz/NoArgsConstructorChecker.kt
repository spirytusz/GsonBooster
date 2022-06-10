package com.spirytusz.booster.processor.check.clazz

import com.spirytusz.booster.processor.base.check.ClassChecker
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner

internal class NoArgsConstructorChecker(private val messageLogger: MessageLogger) : ClassChecker {

    override fun check(classScanner: ClassScanner) {
        val noDefaultValueConstructParams = classScanner.ktFields.asSequence().filter {
            it.declarationScope == DeclarationScope.PRIMARY_CONSTRUCTOR
        }.filter {
            it.initializer == FieldInitializer.NONE
        }.toList()

        if (noDefaultValueConstructParams.isEmpty()) {
            return
        }

        noDefaultValueConstructParams.forEach {
            messageLogger.error("constructor param has no default value", it)
        }

        val noDefaultValueConstructParamNames = noDefaultValueConstructParams.map { it.fieldName }
        throw WithoutNoArgsConstructorException(
            "no default value fields in " +
                    "[${classScanner.classKtType.rawType}] constructor: " +
                    "$noDefaultValueConstructParamNames"
        )
    }

    private class WithoutNoArgsConstructorException(msg: String) : Exception(msg)
}