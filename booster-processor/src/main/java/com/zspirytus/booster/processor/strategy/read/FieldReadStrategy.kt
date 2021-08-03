package com.zspirytus.booster.processor.strategy.read

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.PrimitiveKType

internal class FieldReadStrategy : IFieldReadStrategy {

    private val primitiveTypeReadStrategy by lazy {
        PrimitiveFieldReadStrategy()
    }

    private val objectTypeReadStrategy by lazy {
        ObjectFieldReadStrategy()
    }

    override fun read(kField: KField): CodeBlock {
        return if (kField.kType is PrimitiveKType) {
            primitiveTypeReadStrategy.read(kField)
        } else {
            objectTypeReadStrategy.read(kField)
        }
    }
}