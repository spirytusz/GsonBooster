package com.spirytusz.booster.processor.check.field

import com.spirytusz.booster.processor.base.check.FieldChecker
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner

internal class FieldNamingChecker(private val messageLogger: MessageLogger) : FieldChecker {

    companion object {
        private val keywords = mutableSetOf(
            "as?", "as", "break", "class", "continue",
            "do", "else", "false", "for", "fun",
            "if", "in", "!in", "interface", "is",
            "!is", "null", "object", "package", "return",
            "super", "this", "throw", "true", "try",
            "typealias", "typeof", "val", "var", "when",
            "while", "by", "catch", "constructor", "delegate",
            "dynamic", "field", "file", "finally", "get",
            "import", "init", "param", "property", "receiver",
            "set", "setparam", "value", "where", "actual",
            "abstract", "annotation", "companion", "const",
            "crossinline", "data", "enum", "expect", "external",
            "final", "infix", "inline", "inner", "internal",
            "lateinit", "noinline", "open", "operator", "out",
            "override", "private", "protected", "public", "reified",
            "sealed", "suspend", "tailrec", "vararg"
        )
    }

    override fun check(classScanner: ClassScanner, ktField: KtField) {
        if (ktField.fieldName !in keywords) {
            return
        }

        messageLogger.error("invalid fieldName ${ktField.fieldName}", ktField)
        throw FieldNamedByKotlinKeywordException(
            "invalid fieldName [${ktField.fieldName}]" +
                    " at ${classScanner.classKtType.rawType}"
        )
    }

    private class FieldNamedByKotlinKeywordException(msg: String) : Exception(msg)
}