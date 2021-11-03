package com.spirytusz.booster.processor.gen.api.funcgen.write.types

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.write.types.base.TypeWriteCodeGenerator

object TypeWriteCodeGeneratorFactory {

    fun create(propertyDescriptor: PropertyDescriptor): TypeWriteCodeGenerator {
        return when {
            propertyDescriptor.type.isPrimitive() -> PrimitiveTypeWriteCodeGenerator()
            propertyDescriptor.type.isObject() -> ObjectTypeWriteCodeGenerator()
            propertyDescriptor.type.isArray() -> CollectionTypeWriteCodeGenerator()
            else -> EnumTypeWriteCodeGenerator()
        }
    }
}