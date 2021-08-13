package com.spirytusz.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.CodeBlock
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.EnumKType

internal class EnumFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        if (kField.nullable) {
            codeBlock.addStatement("${kField.fetchFlagFieldName} = true")
        }
        val enumKType = kField.kType as EnumKType
        codeBlock.addStatement(
            """
            val peeked = ${READER}.peek()
            if (peeked == %T.%L) {
                %L = %T.valueOf(${READER}.nextString())
            } else if (peeked == %T.NULL) {
                ${READER}.nextNull()
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