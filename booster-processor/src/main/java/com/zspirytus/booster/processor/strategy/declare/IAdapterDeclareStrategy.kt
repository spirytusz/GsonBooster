package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.KType

internal interface IAdapterDeclareStrategy {

    fun declare(kType: KType): PropertySpec?
}