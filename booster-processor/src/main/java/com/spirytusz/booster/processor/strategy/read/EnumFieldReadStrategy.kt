package com.spirytusz.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.EnumKType
import com.squareup.kotlinpoet.CodeBlock

/**
 * Input: [KField] = val testEnum: TestEnum = TestEnum.HI
 *
 * Output:
 * val peeked = reader.peek()
 * if (peeked == JsonToken.STRING) {
 *     testEnum = TestEnum.valueOf(reader.nextString())
 * } else if (peeked == JsonToken.NULL) {
 *     reader.nextNull()
 * } else {
 *     reader.skipValue()
 * }
 */
class EnumFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        val fetchFlagStatement = "${kField.fetchFlagFieldName} = true"
        val enumKType = kField.kType as EnumKType
        codeBlock.addStatement(
            """
            val peeked = ${READER}.peek()
            if (peeked == %T.%L) {
                %L = %T.valueOf(${READER}.nextString())
                ${if (kField.nullable) fetchFlagStatement else ""}
            } else if (peeked == %T.NULL) {
                ${READER}.nextNull()
                ${if (kField.nullable) fetchFlagStatement else ""}
            } else {
                ${READER}.skipValue()
            }
            """.trimIndent(),
            JsonToken::class.java,
            enumKType.jsonTokenName,
            kField.fieldName,
            enumKType.className,
            JsonToken::class.java
        )
        return codeBlock.build()
    }
}