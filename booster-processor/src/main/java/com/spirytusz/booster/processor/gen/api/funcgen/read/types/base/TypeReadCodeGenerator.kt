package com.spirytusz.booster.processor.gen.api.funcgen.read.types.base

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.squareup.kotlinpoet.CodeBlock

interface TypeReadCodeGenerator {

    fun generate(codeBlock: CodeBlock.Builder, propertyDescriptor: PropertyDescriptor)
}