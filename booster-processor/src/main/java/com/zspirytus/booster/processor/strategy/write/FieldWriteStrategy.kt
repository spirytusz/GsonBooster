package com.zspirytus.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.PrimitiveKType

internal class FieldWriteStrategy : IFieldWriteStrategy {
    private val primitiveTypeWriteStrategy by lazy {
        PrimitiveFieldWriteStrategy()
    }

    private val objectTypeWriteStrategy by lazy {
        ObjectTypeFieldWriteStrategy()
    }

    override fun write(kField: KField): CodeBlock {
        return if (kField.kType is PrimitiveKType) {
            primitiveTypeWriteStrategy.write(kField)
        } else {
            objectTypeWriteStrategy.write(kField)
        }
    }
}