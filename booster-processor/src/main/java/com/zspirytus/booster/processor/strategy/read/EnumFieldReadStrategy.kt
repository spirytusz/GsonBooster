package com.zspirytus.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.const.READER
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.EnumKType

internal class EnumFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        if (kField.nullable) {
            codeBlock.addStatement("${kField.fetchFlagFieldName} = true")
        }
        val enumKType = kField.kType as EnumKType
        codeBlock.addStatement(
            """
            if (%L.peek() != %T.NULL) {
                %L = %T.valueOf(%L.nextString())
            } else {
                %L.nextNull()
            }
            """.trimIndent(),
            READER,
            JsonToken::class.java,
            kField.fieldName,
            enumKType.className,
            READER,
            READER
        )
        return codeBlock.build()
    }
}