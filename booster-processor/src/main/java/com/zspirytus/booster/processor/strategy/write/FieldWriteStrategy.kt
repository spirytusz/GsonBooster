package com.zspirytus.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.BackoffKType
import com.zspirytus.booster.processor.data.type.CollectionKType
import com.zspirytus.booster.processor.data.type.ObjectKType
import com.zspirytus.booster.processor.data.type.PrimitiveKType

internal class FieldWriteStrategy : IFieldWriteStrategy {
    private val primitiveTypeWriteStrategy by lazy {
        PrimitiveFieldWriteStrategy()
    }

    private val objectTypeWriteStrategy by lazy {
        ObjectTypeFieldWriteStrategy()
    }

    private val collectionTypeStrategy by lazy {
        CollectionFieldWriteStrategy()
    }

    override fun write(kField: KField): CodeBlock {
        return when (kField.kType) {
            is PrimitiveKType -> {
                primitiveTypeWriteStrategy.write(kField)
            }
            is ObjectKType -> {
                objectTypeWriteStrategy.write(kField)
            }
            is CollectionKType -> {
                collectionTypeStrategy.write(kField)
            }
            else -> {
                objectTypeWriteStrategy.write(kField)
            }
        }
    }
}