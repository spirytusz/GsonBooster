package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.type.KType

internal class BackoffAdapterDeclareStrategy : IAdapterDeclareStrategy {
    override fun declare(kType: KType): PropertySpec? {
        return PropertySpec.builder("", kType.typeName, KModifier.PRIVATE).build()
    }
}