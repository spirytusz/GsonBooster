package com.zspirytus.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.const.OBJECT
import com.zspirytus.booster.processor.const.WRITER
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.CollectionKType
import com.zspirytus.booster.processor.data.type.PrimitiveKType

internal class CollectionFieldWriteStrategy : IFieldWriteStrategy {
    override fun write(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        return if (PrimitiveKType.isPrimitive(collectionKType.genericType)) {
            writeGenericsPrimitiveCollections(kField)
        } else {
            writeGenericsObjectCollections(kField)
        }
    }

    private fun writeGenericsPrimitiveCollections(kField: KField): CodeBlock {
        return if (kField.nullable) {
            CodeBlock.Builder().addStatement(
                "%L.name(%S)", WRITER, kField.keys.first()
            ).addStatement(
                """
                    val %L = %L.%L
                    if (%L != null) {
                        %L.beginArray()
                        %L.forEach {
                            %L.value(it)
                        }
                        %L.endArray()
                    } else {
                        %L.nullValue()
                    }
                """.trimIndent(),
                kField.fieldName,
                OBJECT,
                kField.fieldName,
                kField.fieldName,
                WRITER,
                kField.fieldName,
                WRITER,
                WRITER,
                WRITER
            )
        } else {
            CodeBlock.Builder().addStatement(
                "%L.name(%S)", WRITER, kField.keys.first()
            ).addStatement(
                """
                    %L.beginArray()
                    %L.%L.forEach {
                        %L.value(it)
                    }
                    %L.endArray()
                """.trimIndent(),
                WRITER,
                OBJECT,
                kField.fieldName,
                WRITER,
                WRITER
            )
        }.build()
    }

    private fun writeGenericsObjectCollections(kField: KField): CodeBlock {
        return if (kField.nullable) {
            CodeBlock.Builder().addStatement(
                """
                   val %L = %L.%L
                    if (%L != null) {
                        %L.beginArray()
                        %L.forEach {
                            %L.write(%L, it)
                        }
                        %L.endArray()
                    } else {
                        %L.nullValue()
                    }
                """.trimIndent(),
                kField.fieldName,
                OBJECT,
                kField.fieldName,
                kField.fieldName,
                WRITER,
                kField.fieldName,
                kField.kType.adapterFieldName,
                WRITER,
                WRITER,
                WRITER
            )
        } else {
            CodeBlock.Builder().addStatement(
                """
                    %L.beginArray()
                    %L.%L.forEach {
                        %L.write(%L, it)
                    }
                    %L.endArray()
                """.trimIndent(),
                WRITER,
                OBJECT,
                kField.fieldName,
                kField.kType.adapterFieldName,
                WRITER,
                WRITER
            )
        }.build()
    }
}