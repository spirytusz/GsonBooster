package com.spirytusz.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.spirytusz.booster.processor.const.OBJECT
import com.spirytusz.booster.processor.const.WRITER
import com.spirytusz.booster.processor.data.KField

internal class EnumFieldWriteStrategy : IFieldWriteStrategy {
    override fun write(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        codeBlock.addStatement("%L.name(%S)", WRITER, kField.keys.first())
        if (kField.nullable) {
            codeBlock.addStatement(
                "val %L = %L.%L?.name",
                kField.fieldName,
                OBJECT,
                kField.fieldName
            )
            codeBlock.addStatement(
                """
                if (%L != null) {
                    %L.value(%L)
                } else {
                    %L.nullValue()
                }
                """.trimIndent(),
                kField.fieldName,
                WRITER,
                kField.fieldName,
                WRITER
            )
        } else {
            codeBlock.addStatement("%L.value(%L.%L.name)", WRITER, OBJECT, kField.fieldName)
        }
        return codeBlock.build()
    }
}