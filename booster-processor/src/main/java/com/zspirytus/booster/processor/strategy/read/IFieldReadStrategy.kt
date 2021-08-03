package com.zspirytus.booster.processor.strategy.read

import com.squareup.kotlinpoet.CodeBlock
import com.zspirytus.booster.processor.data.KField

internal interface IFieldReadStrategy {

    fun read(kField: KField): CodeBlock
}