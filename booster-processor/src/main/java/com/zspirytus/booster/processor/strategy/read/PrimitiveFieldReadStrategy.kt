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
            if (%L.peek() != %T.NULL) {
                %L = %L.next${primitiveKType.getPrimitiveTypeNameForJsonReader()}()
            } else {
                %L.nextNull()
            }
            """.trimIndent(),
            READER,
            JsonToken::class.java,
            kField.fieldName,
            READER,
            READER
        )
        return codeBlock.build()
    }
}