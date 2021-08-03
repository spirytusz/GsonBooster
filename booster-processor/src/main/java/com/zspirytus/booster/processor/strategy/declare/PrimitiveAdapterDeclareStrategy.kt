package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.KField

internal class PrimitiveAdapterDeclareStrategy : IAdapterDeclareStrategy {
    override fun declare(kField: KField): PropertySpec? {
        return null
    }
}