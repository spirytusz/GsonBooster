package com.spirytusz.booster.processor.strategy.read

import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.CollectionKType
import com.spirytusz.booster.processor.data.type.EnumKType
import com.spirytusz.booster.processor.data.type.ObjectKType
import com.spirytusz.booster.processor.data.type.PrimitiveKType
import com.squareup.kotlinpoet.CodeBlock

class FieldReadStrategy : IFieldReadStrategy {

    private val primitiveTypeReadStrategy by lazy {
        PrimitiveFieldReadStrategy()
    }

    private val objectTypeReadStrategy by lazy {
        ObjectFieldReadStrategy()
    }

    private val collectionFieldReadStrategy by lazy {
        CollectionFieldReadStrategy()
    }

    private val enumFieldReadStrategy by lazy {
        EnumFieldReadStrategy()
    }

    override fun read(kField: KField): CodeBlock {
        return when (kField.kType) {
            is PrimitiveKType -> {
                primitiveTypeReadStrategy.read(kField)
            }
            is ObjectKType -> {
                objectTypeReadStrategy.read(kField)
            }
            is CollectionKType -> {
                collectionFieldReadStrategy.read(kField)
            }
            is EnumKType -> {
                enumFieldReadStrategy.read(kField)
            }
            else -> {
                objectTypeReadStrategy.read(kField)
            }
        }
    }
}