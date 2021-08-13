package com.spirytusz.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.spirytusz.booster.processor.data.KField

internal interface IFieldWriteStrategy {
    fun write(kField: KField): CodeBlock
}