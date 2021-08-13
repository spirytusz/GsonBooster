package com.spirytusz.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.CollectionKType
import com.spirytusz.booster.processor.data.type.EnumKType
import com.spirytusz.booster.processor.data.type.ObjectKType
import com.spirytusz.booster.processor.data.type.PrimitiveKType

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

    private val enumTypeWriteStrategy by lazy {
        EnumFieldWriteStrategy()
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
            is EnumKType -> {
                enumTypeWriteStrategy.write(kField)
            }
            else -> {
                objectTypeWriteStrategy.write(kField)
            }
        }
    }
}