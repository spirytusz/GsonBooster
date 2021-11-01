package com.spirytusz.booster.processor.gen.api.funcgen.read.types

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.read.types.base.TypeReadCodeGenerator
import com.spirytusz.booster.processor.gen.const.Constants.READER
import com.squareup.kotlinpoet.CodeBlock

class PrimitiveTypeReadCodeGenerator : TypeReadCodeGenerator {

    override fun generate(codeBlock: CodeBlock.Builder, propertyDescriptor: PropertyDescriptor) {
        val typeDescriptor = propertyDescriptor.type
        val jsonTokenName = typeDescriptor.jsonTokenName

        codeBlock.beginControlFlow("if ($READER.peek() == %T.NULL)", JsonToken::class)
        codeBlock.addStatement("$READER.nextNull()")
        if (typeDescriptor.nullable()) {
            codeBlock.addStatement("${propertyDescriptor.fieldName} = null")
        }
        codeBlock.endControlFlow()

        codeBlock.beginControlFlow("else")
        codeBlock.addStatement("${propertyDescriptor.fieldName} = $READER.${jsonTokenName.nextFunExp}")
        codeBlock.endControlFlow()
    }
}