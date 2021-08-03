package com.zspirytus.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.const.OBJECT
import com.zspirytus.booster.processor.const.WRITER
import com.zspirytus.booster.processor.data.KField

internal class PrimitiveFieldWriteStrategy : IFieldWriteStrategy {
    override fun write(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        if (kField.nullable) {
            codeBlock.addStatement(
                "val %L = %L.%L",
                kField.fieldName,
                OBJECT,
                kField.fieldName
            )
            codeBlock.addStatement(
                """
                if (%L != null) {
                    %L.value(%L)"
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
            codeBlock.addStatement("%L.value(%L.%L)", WRITER, OBJECT, kField.fieldName)
        }
        return codeBlock.build()
    }
}