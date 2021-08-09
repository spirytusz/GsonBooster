package com.zspirytus.booster.processor.strategy.read

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.*

internal class FieldReadStrategy : IFieldReadStrategy {

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