package com.zspirytus.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.const.NULLABLE_FIELD_FLAG_PREFIX
import com.zspirytus.booster.processor.const.READER
import com.zspirytus.booster.processor.data.KField

internal class ObjectFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        if (kField.nullable) {
            codeBlock.addStatement("$NULLABLE_FIELD_FLAG_PREFIX${kField.fieldName} = true")
        }
        codeBlock.addStatement(
            """
            if (%L.peek() != %T.NULL) {
                %L = %L.read(%L)
            } else {
                %L.nextNull()
            }
            """.trimIndent(),
            READER,
            JsonToken::class.java,
            kField.fieldName,
            kField.kType.adapterFieldName,
            READER,
            READER
        )
        return codeBlock.build()
    }
}