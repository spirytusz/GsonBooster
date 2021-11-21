package com.spirytusz.booster.processor.gen.api.funcgen.write.types

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.extension.getTypeAdapterFieldName
import com.spirytusz.booster.processor.gen.api.funcgen.write.types.base.TypeWriteCodeGenerator
import com.spirytusz.booster.processor.gen.const.Constants.OBJECT
import com.spirytusz.booster.processor.gen.const.Constants.WRITER
import com.squareup.kotlinpoet.FunSpec

class ObjectTypeWriteCodeGenerator : TypeWriteCodeGenerator {
    override fun generate(codeBlock: FunSpec.Builder, propertyDescriptor: PropertyDescriptor) {
        val type = propertyDescriptor.type
        val typeAdapterFieldName = type.getTypeAdapterFieldName()
        if (type.nullable()) {
            // if
            codeBlock.beginControlFlow("if (${OBJECT}.${propertyDescriptor.fieldName} != null)")
            codeBlock.addStatement(
                "$OBJECT.${propertyDescriptor.fieldName}?.let { $typeAdapterFieldName.write($WRITER, it) }"
            )
            codeBlock.endControlFlow()

            // else
            codeBlock.beginControlFlow("else")
            codeBlock.addStatement("$WRITER.nullValue()")
            codeBlock.endControlFlow()
        } else {
            codeBlock.addStatement(
                "$typeAdapterFieldName.write($WRITER, $OBJECT.${propertyDescriptor.fieldName})"
            )
        }
    }
}