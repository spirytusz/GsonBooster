package com.spirytusz.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.PrimitiveKType
import com.squareup.kotlinpoet.CodeBlock

/**
 * Input: [KField] = val longValue: Long = 0L
 *
 * Output:
 * val peeked = reader.peek()
 * if (peeked == JsonToken.NUMBER) {
 *     longValue = reader.nextLong()
 * } else if (peeked == JsonToken.NULL) {
 *     reader.nextNull()
 * } else {
 *     reader.skipValue()
 * }
 */
class PrimitiveFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        val fetchFlagStatement = "${kField.fetchFlagFieldName} = true"
        val primitiveKType = kField.kType as PrimitiveKType
        val toFloatStatement = if (primitiveKType.isFloat()) {
            ".toFloat()"
        } else {
            ""
        }
        codeBlock.addStatement(
            """
            val peeked = ${READER}.peek()
            if (peeked == %T.%L) {
                %L = ${READER}.next${primitiveKType.getPrimitiveTypeNameForJsonReader()}()${toFloatStatement}
                ${if (kField.nullable) fetchFlagStatement else ""}
            } else if (peeked == %T.NULL) {
                ${READER}.nextNull()
                ${if (kField.nullable) fetchFlagStatement else ""}
            } else {
                ${READER}.skipValue()
            }
            """.trimIndent(),
            JsonToken::class.java,
            primitiveKType.jsonTokenName,
            kField.fieldName,
            JsonToken::class.java
        )
        return codeBlock.build()
    }
}