package com.spirytusz.booster.processor.strategy.write

import com.spirytusz.booster.processor.const.OBJECT
import com.spirytusz.booster.processor.const.WRITER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.CollectionKType
import com.spirytusz.booster.processor.data.type.PrimitiveKType
import com.squareup.kotlinpoet.CodeBlock

/**
 * Input: [KField] = val listLong: List<Long>
 *
 * Output:
 * writer.beginArray()
 * obj.listLong.forEach {
 *     writer.value(it)
 * }
 * writer.endArray()
 *
 *
 * Input: [KField] = val listFoo: List<Foo>
 *
 * Output:
 * writer.beginArray()
 * obj.listFoo.forEach {
 *     fooTypeAdapter.write(writer, it)
 * }
 * writer.endArray()
 */
class CollectionFieldWriteStrategy : IFieldWriteStrategy {
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
                "$WRITER.name(%S)", kField.keys.first()
            ).addStatement(
                """
                    val %L = $OBJECT.%L
                    if (%L != null) {
                        $WRITER.beginArray()
                        %L.forEach {
                            $WRITER.value(it)
                        }
                        $WRITER.endArray()
                    } else {
                        $WRITER.nullValue()
                    }
                """.trimIndent(),
                kField.fieldName,
                kField.fieldName,
                kField.fieldName,
                kField.fieldName
            )
        } else {
            CodeBlock.Builder().addStatement(
                "$WRITER.name(%S)", kField.keys.first()
            ).addStatement(
                """
                    $WRITER.beginArray()
                    $OBJECT.%L.forEach {
                        $WRITER.value(it)
                    }
                    $WRITER.endArray()
                """.trimIndent(),
                kField.fieldName
            )
        }.build()
    }

    private fun writeGenericsObjectCollections(kField: KField): CodeBlock {
        return if (kField.nullable) {
            CodeBlock.Builder().addStatement(
                "$WRITER.name(%S)", kField.keys.first()
            ).addStatement(
                """
                   val %L = $OBJECT.%L
                    if (%L != null) {
                        $WRITER.beginArray()
                        %L.forEach {
                            %L.write($WRITER, it)
                        }
                        $WRITER.endArray()
                    } else {
                        $WRITER.nullValue()
                    }
                """.trimIndent(),
                kField.fieldName,
                kField.fieldName,
                kField.fieldName,
                kField.fieldName,
                kField.kType.adapterFieldName
            )
        } else {
            CodeBlock.Builder().addStatement(
                "$WRITER.name(%S)", kField.keys.first()
            ).addStatement(
                """
                    $WRITER.beginArray()
                    $OBJECT.%L.forEach {
                        %L.write($WRITER, it)
                    }
                    $WRITER.endArray()
                """.trimIndent(),
                kField.fieldName,
                kField.kType.adapterFieldName
            )
        }.build()
    }
}