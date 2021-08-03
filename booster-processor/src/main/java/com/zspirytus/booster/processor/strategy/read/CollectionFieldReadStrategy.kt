package com.zspirytus.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
import com.zspirytus.booster.processor.const.READER
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.CollectionKType
import com.zspirytus.booster.processor.data.type.PrimitiveKType
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
        val primitiveTypeName =
            PrimitiveKType.getPrimitiveTypeNameForJsonReader(collectionKType.genericType)
        return CodeBlock.Builder().addStatement(
            """
            if (%L.peek() != %T.NULL) {
                val tempList = mutableListOf<%T>()
                while (%L.hasNext()) {
                    if (%L.peek() != %T.NULL) {
                        tempList.add(%L.next$primitiveTypeName())
                    }
                }
                %L = tempList%L
            } else {
                %L.nextNull()
            }
            """.trimIndent(),
            READER,
            JsonToken::class.java,
            collectionKType.genericType.kotlinType(),
            READER,
            READER,
            JsonToken::class.java,
            READER,
            kField.fieldName,
            if (isSet(collectionKType)) {
                ".toSet()"
            } else {
                ""
            },
            READER
        ).build()
    }

    private fun readGenericsObjectCollections(kField: KField): CodeBlock {
        val collectionKType = kField.kType as CollectionKType
        return CodeBlock.Builder().addStatement(
            """
                if (%L.peek() != %T.NULL) {
                val tempList = mutableListOf<%L>()
                while (%L.hasNext()) {
                    if (%L.peek() != %T.NULL) {
                        tempList.add(%L.read(%L))
                    }
                }
                %L = tempList%L
            } else {
                %L.nextNull()
            }
            """.trimIndent(),
            READER,
            JsonToken::class.java,
            collectionKType.genericType,
            READER,
            READER,
            JsonToken::class.java,
            collectionKType.adapterFieldName,
            READER,
            kField.fieldName,
            if (isSet(collectionKType)) {
                ".toSet()"
            } else {
                ""
            },
            READER
        ).build()
    }
}