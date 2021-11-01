package com.spirytusz.booster.processor.gen.api.funcgen.read.types

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.read.types.base.TypeReadCodeGenerator
import com.spirytusz.booster.processor.gen.const.Constants.READER
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterFieldName
import com.squareup.kotlinpoet.CodeBlock

class ObjectTypeReadCodeGenerator : TypeReadCodeGenerator {

    override fun generate(codeBlock: CodeBlock.Builder, propertyDescriptor: PropertyDescriptor) {
        val typeDescriptor = propertyDescriptor.type
        val typeAdapterFieldName = typeDescriptor.getTypeAdapterFieldName()

        codeBlock.beginControlFlow("if ($READER.peek() == %T.NULL)", JsonToken::class)
        codeBlock.addStatement("$READER.nextNull()")
        if (typeDescriptor.nullable()) {
            codeBlock.addStatement("${propertyDescriptor.fieldName} = null")
        }
        codeBlock.endControlFlow()

        codeBlock.beginControlFlow("else")
        codeBlock.addStatement("$typeAdapterFieldName.read($READER)?.let { ${propertyDescriptor.fieldName} = it }")
        codeBlock.endControlFlow()
    }
}