package com.spirytusz.booster.processor.check.condition

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.spirytusz.booster.processor.check.api.AbstractClassPropertiesChecker
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.extension.error
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class KotlinKeywordPropertyChecker(
    private val environment: SymbolProcessorEnvironment
) : AbstractClassPropertiesChecker() {

    companion object {
        private const val TAG = "KotlinKeywordPropertyChecker"

        // LinkedHashSet
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

    override fun calculateInvalidProperties(classScanner: AbstractClassScanner): Set<PropertyDescriptor> {
        return classScanner.allProperties.filterNot { it.transient }
            .filter { it.fieldName in keywords }.toSet()
    }

    override fun onError(
        classScanner: AbstractClassScanner,
        invalidProperties: Set<PropertyDescriptor>
    ) {
        val invalidPropertyNames = invalidProperties.map { it.fieldName }
        val msg = "properties: $invalidPropertyNames named by kotlin keywords"
        environment.logger.error(TAG, msg, classScanner.ksClass)
    }
}