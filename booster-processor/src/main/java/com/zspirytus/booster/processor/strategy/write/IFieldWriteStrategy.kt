package com.zspirytus.booster.processor.strategy.write

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.data.KField

internal interface IFieldWriteStrategy {
    fun write(kField: KField): CodeBlock
}