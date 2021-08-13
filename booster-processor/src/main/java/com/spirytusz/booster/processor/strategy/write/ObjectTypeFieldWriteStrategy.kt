package com.spirytusz.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.spirytusz.booster.processor.const.OBJECT
import com.spirytusz.booster.processor.const.WRITER
import com.spirytusz.booster.processor.data.KField

internal class ObjectTypeFieldWriteStrategy: IFieldWriteStrategy {
    override fun write(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        codeBlock.addStatement("%L.name(%S)", WRITER, kField.keys.first())
        if (kField.nullable) {
            codeBlock.addStatement(
                "val %L = %L.%L",
                kField.fieldName,
                OBJECT,
                kField.fieldName
            )
            codeBlock.addStatement("""
                        if (%L != null) {
                            %L.write(%L, %L)
                        } else {
                            %L.nullValue()
                        }
                    """.trimIndent(),
                kField.fieldName,
                kField.kType.adapterFieldName,
                WRITER,
                kField.fieldName,
                WRITER
            )
        } else {
            codeBlock.addStatement(
                "%L.write(%L, %L.%L)",
                kField.kType.adapterFieldName,
                WRITER,
                OBJECT,
                kField.fieldName
            )
        }
        return codeBlock.build()
    }
}