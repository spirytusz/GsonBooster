package com.spirytusz.booster.processor.gen.api.funcgen.read.types

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.config.BoosterGenConfig
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.read.types.base.TypeReadCodeGenerator
import com.spirytusz.booster.processor.gen.const.Constants
import com.spirytusz.booster.processor.gen.const.Constants.READER
import com.spirytusz.booster.processor.gen.const.Constants.TRIGGER_CRASH_COMMENT
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterFieldName
import com.squareup.kotlinpoet.CodeBlock

class ObjectTypeReadCodeGenerator : TypeReadCodeGenerator {

    override fun generate(
        codeBlock: CodeBlock.Builder,
        propertyDescriptor: PropertyDescriptor,
        config: BoosterGenConfig
    ) {
        val typeDescriptor = propertyDescriptor.type
        val typeAdapterFieldName = typeDescriptor.getTypeAdapterFieldName()

        codeBlock.beginControlFlow("if ($READER.peek() == %T.NULL)", JsonToken::class)
        when {
            typeDescriptor.nullable() -> {
                codeBlock.addStatement("$READER.nextNull()")
                codeBlock.addStatement("${propertyDescriptor.fieldName} = null")
            }
            config.fromJsonNullSafe -> {
                codeBlock.addStatement("$READER.nextNull()")
            }
            else -> {
                codeBlock.addStatement(TRIGGER_CRASH_COMMENT, typeDescriptor.jsonTokenName.token)
                codeBlock.addStatement("$READER.beginObject()")
            }
        }
        codeBlock.endControlFlow()

        codeBlock.beginControlFlow("else")
        codeBlock.addStatement("$typeAdapterFieldName.read($READER)?.let { ${propertyDescriptor.fieldName} = it }")
        codeBlock.endControlFlow()
    }
}