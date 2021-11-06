package com.spirytusz.booster.processor.gen.api.funcgen.read.types

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.config.BoosterGenConfig
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.read.types.base.TypeReadCodeGenerator
import com.spirytusz.booster.processor.gen.const.Constants
import com.spirytusz.booster.processor.gen.const.Constants.READER
import com.spirytusz.booster.processor.gen.const.Constants.TRIGGER_CRASH_COMMENT
import com.squareup.kotlinpoet.CodeBlock

class EnumTypeReadCodeGenerator : TypeReadCodeGenerator {

    override fun generate(
        codeBlock: CodeBlock.Builder,
        propertyDescriptor: PropertyDescriptor,
        config: BoosterGenConfig
    ) {
        val typeDescriptor = propertyDescriptor.type
        val jsonTokenName = typeDescriptor.jsonTokenName

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
                codeBlock.addStatement(TRIGGER_CRASH_COMMENT, jsonTokenName.token)
                codeBlock.addStatement("$READER.${jsonTokenName.nextFunExp}")
            }
        }
        codeBlock.endControlFlow()

        codeBlock.beginControlFlow("else")
        codeBlock.addStatement("val enumName = ${READER}.${jsonTokenName.nextFunExp}")
        codeBlock.addStatement("${propertyDescriptor.fieldName} = ${typeDescriptor.flattenSimple()}.valueOf(enumName)")
        codeBlock.endControlFlow()
    }
}