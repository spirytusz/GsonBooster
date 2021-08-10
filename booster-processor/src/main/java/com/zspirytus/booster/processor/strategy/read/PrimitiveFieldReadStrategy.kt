package com.zspirytus.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.const.READER
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.PrimitiveKType

internal class PrimitiveFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        if (kField.nullable) {
            codeBlock.addStatement("${kField.fetchFlagFieldName} = true")
        }
        val primitiveKType = kField.kType as PrimitiveKType
        codeBlock.addStatement(
            """
            val peeked = ${READER}.peek()
            if (peeked == %T.%L) {
                %L = ${READER}.next${primitiveKType.getPrimitiveTypeNameForJsonReader()}()
            } else if (peeked == %T.NULL) {
                ${READER}.nextNull()
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