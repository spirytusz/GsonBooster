package com.spirytusz.booster.processor.strategy.declare

import com.spirytusz.booster.processor.data.type.KType
import com.squareup.kotlinpoet.PropertySpec

/**
 * Input:  [KType] = TestEnum
 *
 * Output: null
 */
class EnumTypeAdapterDeclareStrategy : IAdapterDeclareStrategy {
    override fun declare(kType: KType): PropertySpec? {
        return null
    }
}