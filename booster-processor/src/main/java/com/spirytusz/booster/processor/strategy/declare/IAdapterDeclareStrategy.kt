package com.spirytusz.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.spirytusz.booster.processor.data.type.KType

internal interface IAdapterDeclareStrategy {

    fun declare(kType: KType): PropertySpec?
}