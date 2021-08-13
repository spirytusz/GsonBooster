package com.spirytusz.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.spirytusz.booster.processor.data.type.CollectionKType
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.data.type.PrimitiveKType

internal class CollectionAdapterDeclareStrategy : IAdapterDeclareStrategy {

    var objectAdapterDeclareStrategy: ObjectAdapterDeclareStrategy? = null

    override fun declare(kType: KType): PropertySpec? {
        kType as CollectionKType
        if (PrimitiveKType.isPrimitive(kType.rawType)) {
            return null
        }
        val genericsKType = KType.makeKTypeByTypeName(kType.genericType)
        return objectAdapterDeclareStrategy?.declare(genericsKType)
    }
}