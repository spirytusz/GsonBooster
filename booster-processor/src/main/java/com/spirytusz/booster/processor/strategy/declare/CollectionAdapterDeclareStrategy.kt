package com.spirytusz.booster.processor.strategy.declare

import com.spirytusz.booster.processor.data.type.CollectionKType
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.data.type.PrimitiveKType
import com.squareup.kotlinpoet.PropertySpec

/**
 * Input:  [KType] = List<Foo>
 *
 * Output: private val fooTypeAdapter by lazy { FooTypeAdapter() }
 *
 * Input:  KType = List<Int>
 *
 * Output: null
 */
class CollectionAdapterDeclareStrategy : IAdapterDeclareStrategy {

    var objectAdapterDeclareStrategy: ObjectAdapterDeclareStrategy? = null
    var primitiveAdapterDeclareStrategy: PrimitiveAdapterDeclareStrategy? = null

    override fun declare(kType: KType): PropertySpec? {
        kType as CollectionKType
        val genericsKType = KType.makeKTypeByTypeName(kType.genericType)
        return if (PrimitiveKType.isPrimitive(kType.genericType)) {
            primitiveAdapterDeclareStrategy?.declare(genericsKType)
        } else {
            objectAdapterDeclareStrategy?.declare(genericsKType)
        }
    }
}