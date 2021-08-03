package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.KField

internal class CollectionAdapterDeclareStrategy: IAdapterDeclareStrategy {
    override fun declare(kField: KField): PropertySpec {
        return PropertySpec.builder("", kField.kType.typeName, KModifier.PRIVATE).build()
    }
}