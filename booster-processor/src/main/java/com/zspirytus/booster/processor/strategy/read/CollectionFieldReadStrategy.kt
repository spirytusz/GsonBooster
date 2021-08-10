package com.zspirytus.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
import com.zspirytus.booster.processor.const.NULLABLE_FIELD_FLAG_PREFIX
import com.zspirytus.booster.processor.const.READER
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.CollectionKType
import com.zspirytus.booster.processor.data.type.KType
import com.zspirytus.booster.processor.data.type.PrimitiveKType
import com.zspirytus.booster.processor.extensions.firstChatUpperCase
import com.zspirytus.booster.processor.extensions.kotlinType

internal class CollectionFieldReadStrategy : IFieldReadStrategy {

    override fun read(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        return if (PrimitiveKType.isPrimitive(collectionKType.genericType)) {
            readGenericsPrimitiveCollections(kField)
        } else {
            readGenericsObjectCollections(kField)
        }
    }

    private fun isSet(kType: CollectionKType): Boolean {
        return kType.rawType in listOf(
            ClassName("java.lang", "Set"),
            Set::class.java.asClassName()
        )
    }

    private fun readGenericsPrimitiveCollections(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        val genericsKType = KType.makeKTypeByTypeName(kField.kType.genericType)
        val primitiveTypeName =
            PrimitiveKType.getPrimitiveTypeNameForJsonReader(collectionKType.genericType)
        return CodeBlock.Builder().addStatement(
            """
            val collectionPeeked = ${READER}.peek()
            if (collectionPeeked == %T.%L) {
                val tempList = mutableListOf<%T>()
                ${READER}.beginArray()
                while (${READER}.hasNext()) {
                    val peeked = ${READER}.peek()
                    if (peeked == %T.%L) {
                        tempList.add(${READER}.next$primitiveTypeName())
                    } else if (peeked == %T.NULL) {
                        ${READER}.nextNull()
                    } else {
                        ${READER}.skipValue()
                    }
                }
                ${READER}.endArray()
                %L = tempList%L
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
            if (isSet(collectionKType)) {
                ".toSet()"
            } else {
                ""
            },
            JsonToken::class.java
        ).build()
    }

    private fun readGenericsObjectCollections(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        val genericsKType = KType.makeKTypeByTypeName(collectionKType.genericType)
        val codeBlock = CodeBlock.Builder()
        if (kField.nullable) {
            codeBlock.addStatement("$NULLABLE_FIELD_FLAG_PREFIX${kField.fieldName.firstChatUpperCase()} = true")
        }
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
                    } else if (peeked == %T.NULL) {
                        ${READER}.nextNull()
                    } else {
                        ${READER}.skipValue()
                    }
                }
                ${READER}.endArray()
                %L = tempList%L
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
            if (isSet(collectionKType)) {
                ".toSet()"
            } else {
                ""
            },
            JsonToken::class.java
        ).build()
    }
}