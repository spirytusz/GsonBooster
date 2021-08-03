package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.KField

internal interface IAdapterDeclareStrategy {

    fun declare(kField: KField): PropertySpec?
}