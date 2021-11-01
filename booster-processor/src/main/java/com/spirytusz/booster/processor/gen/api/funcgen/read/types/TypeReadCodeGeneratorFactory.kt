package com.spirytusz.booster.processor.gen.api.funcgen.read.types

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.read.types.base.TypeReadCodeGenerator

object TypeReadCodeGeneratorFactory {

    fun create(propertyDescriptor: PropertyDescriptor): TypeReadCodeGenerator {
        return when {
            propertyDescriptor.type.isPrimitive() -> PrimitiveTypeReadCodeGenerator()
            propertyDescriptor.type.isArray() -> CollectionTypeReadCodeGenerator()
            propertyDescriptor.type.isObject() -> ObjectTypeReadCodeGenerator()
            else -> EnumTypeReadCodeGenerator()
        }
    }
}