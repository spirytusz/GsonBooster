package com.spirytusz.booster.processor.gen.api.funcgen.write.types

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.write.types.base.TypeWriteCodeGenerator
import com.spirytusz.booster.processor.gen.const.Constants.OBJECT
import com.spirytusz.booster.processor.gen.const.Constants.WRITER
import com.squareup.kotlinpoet.FunSpec

class EnumTypeWriteCodeGenerator : TypeWriteCodeGenerator {
    override fun generate(codeBlock: FunSpec.Builder, propertyDescriptor: PropertyDescriptor) {
        val type = propertyDescriptor.type
        if (type.nullable()) {
            // if
            codeBlock.beginControlFlow("if (${OBJECT}.${propertyDescriptor.fieldName} != null)")
            codeBlock.addStatement("${WRITER}.value($OBJECT.${propertyDescriptor.fieldName}.name)")
            codeBlock.endControlFlow()

            // else
            codeBlock.beginControlFlow("else")
            codeBlock.addStatement("$WRITER.nullValue()")
            codeBlock.endControlFlow()
        } else {
            codeBlock.addStatement("$WRITER.value($OBJECT.${propertyDescriptor.fieldName}.name)")
        }
    }
}