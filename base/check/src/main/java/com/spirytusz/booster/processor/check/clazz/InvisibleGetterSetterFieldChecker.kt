package com.spirytusz.booster.processor.check.clazz

import com.spirytusz.booster.processor.base.check.ClassChecker
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner

internal class InvisibleGetterSetterFieldChecker(private val messageLogger: MessageLogger) :
    ClassChecker {

    override fun check(classScanner: ClassScanner) {
        val invisibleGetterSetterFields = classScanner.ktFields.asSequence().filter {
            !it.transient
        }.filter {
            it.declarationScope in listOf(DeclarationScope.BODY, DeclarationScope.SUPER_CLASS)
        }.filter {
            it.isFinal
        }.toList()

        if (invisibleGetterSetterFields.isEmpty()) {
            return
        }

        invisibleGetterSetterFields.forEach {
            messageLogger.error("invisible getter setter field", it)
        }
        val invisibleGetterSetterFieldNames = invisibleGetterSetterFields.map { it.fieldName }

        throw InvisibleGetterSetterException(
            "invisible getter setter field in " +
                    "class [${classScanner.classKtType.rawType}]: " +
                    "$invisibleGetterSetterFieldNames"
        )
    }

    private class InvisibleGetterSetterException(msg: String) : Exception(msg)
}