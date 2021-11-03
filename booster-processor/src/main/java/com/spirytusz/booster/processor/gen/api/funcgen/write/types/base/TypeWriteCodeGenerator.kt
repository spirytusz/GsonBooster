package com.spirytusz.booster.processor.gen.api.funcgen.write.types.base

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.squareup.kotlinpoet.FunSpec

interface TypeWriteCodeGenerator {

    fun generate(codeBlock: FunSpec.Builder, propertyDescriptor: PropertyDescriptor)
}