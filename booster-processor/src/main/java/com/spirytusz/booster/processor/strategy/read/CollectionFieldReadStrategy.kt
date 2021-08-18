package com.spirytusz.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.CollectionKType
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.data.type.PrimitiveKType
import com.spirytusz.booster.processor.extensions.kotlinType
import com.squareup.kotlinpoet.CodeBlock

/**
 * Input: [KField] = val listLong: List<Long>
 *
 * Output:
 * val collectionPeeked = reader.peek()
 * if (collectionPeeked == JsonToken.BEGIN_ARRAY) {
 *     val tempList = mutableListOf<Long>
 *     reader.beginArray()
 *     while (reader.hasNext) {
 *          val peeked = reader.peek()
 *          if (peeked == JsonToken.NUMBER) {
 *              tempList.add(reader.nextLong())
 *          } else if (peeked == JsonToken.NULL) {
 *              reader.nextNull()
 *          } else {
 *              reader.skipValue()
 *          }
 *     }
 *     reader.endArray()
 * } else if (collectionPeeked == JsonToken.NULL) {
 *     reader.nextNull()
 * } else {
 *     reader.skipValue()
 * }
 *
 *
 * Input: KField = val listFoo: List<Foo>
 *
 * Output:
 * val collectionPeeked = reader.peek()
 * if (collectionPeeked == JsonToken.BEGIN_ARRAY) {
 *     val tempList = mutableListOf<Foo>
 *     reader.beginArray()
 *     while (reader.hasNext) {
 *          val peeked = reader.peek()
 *          if (peeked == JsonToken.BEGIN_OBJECT) {
 *              tempList.add(fooTypeAdapter.read(reader))
 *          } else if (peeked == JsonToken.NULL) {
 *              reader.nextNull()
 *          } else {
 *              reader.skipValue()
 *          }
 *     }
 *     reader.endArray()
 * } else if (collectionPeeked == JsonToken.NULL) {
 *     reader.nextNull()
 * } else {
 *     reader.skipValue()
 * }
 */
class CollectionFieldReadStrategy : IFieldReadStrategy {

    override fun read(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        return if (PrimitiveKType.isPrimitive(collectionKType.genericType)) {
            readGenericsPrimitiveCollections(kField)
        } else {
            readGenericsObjectCollections(kField)
        }
    }

    private fun readGenericsPrimitiveCollections(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        val genericsKType = KType.makeKTypeByTypeName(kField.kType.genericType)
        val primitiveTypeName =
            PrimitiveKType.getPrimitiveTypeNameForJsonReader(collectionKType.genericType)
        val codeBlock = CodeBlock.Builder()
        val fetchFlagStatement = "${kField.fetchFlagFieldName} = true"
        return codeBlock.addStatement(
            """
            val collectionPeeked = ${READER}.peek()
            if (collectionPeeked == %T.%L) {
                val tempList = mutableListOf<%T>()
                ${READER}.beginArray()
                while (${READER}.hasNext()) {
                    val peeked = ${READER}.peek()
                    if (peeked == %T.%L) {
                        tempList.add(${READER}.next$primitiveTypeName())
                        ${if (kField.nullable) fetchFlagStatement else ""}
                    } else if (peeked == %T.NULL) {
                        ${READER}.nextNull()
                        ${if (kField.nullable) fetchFlagStatement else ""}
                    } else {
                        ${READER}.skipValue()
                    }
                }
                ${READER}.endArray()
                %L = tempList${if (collectionKType.isSet()) ".toSet()" else ""}
            } else if (collectionPeeked == %T.NULL) {
                ${READER}.nextNull()
            } else {
                ${READER}.skipValue()
            }
            """.trimIndent(),
            JsonToken::class.java,
            collectionKType.jsonTokenName,
            collectionKType.genericType.kotlinType(),
            JsonToken::class.java,
            genericsKType.jsonTokenName,
            JsonToken::class.java,
            kField.fieldName,
            JsonToken::class.java
        ).build()
    }

    private fun readGenericsObjectCollections(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        val genericsKType = KType.makeKTypeByTypeName(collectionKType.genericType)
        val codeBlock = CodeBlock.Builder()
        val fetchFlagStatement = "${kField.fetchFlagFieldName} = true"
        return codeBlock.addStatement(
            """
            val collectionPeeked = ${READER}.peek()
            if (collectionPeeked == %T.%L) {
                val tempList = mutableListOf<%L>()
                ${READER}.beginArray()
                while (${READER}.hasNext()) {
                    val peeked = ${READER}.peek()
                    if (peeked == %T.%L) {
                        tempList.add(%L.read(${READER}))
                        ${if (kField.nullable) fetchFlagStatement else ""}
                    } else if (peeked == %T.NULL) {
                        ${READER}.nextNull()
                        ${if (kField.nullable) fetchFlagStatement else ""}
                    } else {
                        ${READER}.skipValue()
                    }
                }
                ${READER}.endArray()
                %L = tempList${if (collectionKType.isSet()) ".toSet()" else ""}
            } else if (collectionPeeked == %T.NULL) {
                ${READER}.nextNull()
            } else {
                ${READER}.skipValue()
            }
            """.trimIndent(),
            JsonToken::class.java,
            collectionKType.jsonTokenName,
            collectionKType.genericType.kotlinType(),
            JsonToken::class.java,
            genericsKType.jsonTokenName,
            collectionKType.adapterFieldName,
            JsonToken::class.java,
            kField.fieldName,
            JsonToken::class.java
        ).build()
    }
}