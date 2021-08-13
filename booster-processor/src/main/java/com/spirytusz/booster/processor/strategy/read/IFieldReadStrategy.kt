package com.spirytusz.booster.processor.strategy.read

import com.squareup.kotlinpoet.CodeBlock
import com.spirytusz.booster.processor.data.KField

internal interface IFieldReadStrategy {

    fun read(kField: KField): CodeBlock
}