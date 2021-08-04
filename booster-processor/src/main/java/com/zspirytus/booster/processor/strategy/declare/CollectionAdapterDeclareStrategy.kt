package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.type.CollectionKType
import com.zspirytus.booster.processor.data.type.KType
import com.zspirytus.booster.processor.data.type.PrimitiveKType

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