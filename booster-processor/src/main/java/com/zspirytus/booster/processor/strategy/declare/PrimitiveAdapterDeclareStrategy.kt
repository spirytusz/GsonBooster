package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.type.KType

internal class PrimitiveAdapterDeclareStrategy : IAdapterDeclareStrategy {
    override fun declare(kType: KType): PropertySpec? {
        return null
    }
}