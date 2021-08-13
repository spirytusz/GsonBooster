package com.spirytusz.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.CodeBlock
import com.spirytusz.booster.processor.const.NULLABLE_FIELD_FLAG_PREFIX
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.extensions.firstChatUpperCase

internal class ObjectFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        if (kField.nullable) {
            codeBlock.addStatement("$NULLABLE_FIELD_FLAG_PREFIX${kField.fieldName.firstChatUpperCase()} = true")
        }
        codeBlock.addStatement(
            """
            val peeked = ${READER}.peek()
            if (peeked == %T.%L) {
                %L = %L.read(${READER})
            } else if (peeked == %T.NULL) {
                ${READER}.nextNull()
            } else {
                ${READER}.skipValue()
            }
            """.trimIndent(),
            JsonToken::class.java,
            kField.kType.jsonTokenName,
            kField.fieldName,
            kField.kType.adapterFieldName,
            JsonToken::class.java
        )
        return codeBlock.build()
    }
}