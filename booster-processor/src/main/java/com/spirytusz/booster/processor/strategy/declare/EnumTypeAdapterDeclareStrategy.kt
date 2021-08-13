package com.spirytusz.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.spirytusz.booster.processor.data.type.KType

internal class EnumTypeAdapterDeclareStrategy : IAdapterDeclareStrategy {
    override fun declare(kType: KType): PropertySpec? {
        return null
    }
}