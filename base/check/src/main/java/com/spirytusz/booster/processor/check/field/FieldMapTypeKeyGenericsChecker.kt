package com.spirytusz.booster.processor.check.field

import com.spirytusz.booster.processor.base.check.FieldChecker
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner

internal class FieldMapTypeKeyGenericsChecker(private val messageLogger: MessageLogger) :
    FieldChecker {
    override fun check(classScanner: ClassScanner, ktField: KtField) {
        val mapTypeField = ktField.ktType.dfs { jsonTokenName.isMap() }.firstOrNull() ?: return
        val keyGenerics = mapTypeField.generics.first()
        if (keyGenerics.jsonTokenName == JsonTokenName.STRING) {
            return
        }

        messageLogger.error("invalid map key generics type", ktField)
        throw InvalidMapKeyGenericsException(
            "invalid map key generics type [${keyGenerics.rawType}] " +
                    "in field [${ktField.fieldName}] at class " +
                    "[${classScanner.classKtType.rawType}]"
        )
    }

    private class InvalidMapKeyGenericsException(msg: String) : Exception(msg)
}