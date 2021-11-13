package com.spirytusz.booster.processor.gen.api.funcgen.write.types

import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.data.TypeDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.write.types.base.TypeWriteCodeGenerator
import com.spirytusz.booster.processor.gen.const.Constants.OBJECT
import com.spirytusz.booster.processor.gen.const.Constants.WRITER
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterFieldName
import com.spirytusz.booster.processor.gen.extension.getWritingTempFieldName
import com.squareup.kotlinpoet.FunSpec

class CollectionTypeWriteCodeGenerator : TypeWriteCodeGenerator {
    override fun generate(codeBlock: FunSpec.Builder, propertyDescriptor: PropertyDescriptor) {
        addCheckCodeRecursively(codeBlock, propertyDescriptor, propertyDescriptor.type)
    }

    private fun addCheckCodeRecursively(
        codeBlock: FunSpec.Builder,
        propertyDescriptor: PropertyDescriptor,
        typeDescriptor: TypeDescriptor
    ) {
        if (!typeDescriptor.isArray()) {
            addWriteNonCollectionTypeCode(codeBlock, propertyDescriptor, typeDescriptor)
            return
        }

        fun addWriteCollectionCode(
            codeBlock: FunSpec.Builder,
            propertyDescriptor: PropertyDescriptor,
            typeDescriptor: TypeDescriptor
        ) {
            val tempWritingFieldName = typeDescriptor.getWritingTempFieldName(propertyDescriptor.fieldName)
            val genericTypeDescriptor = typeDescriptor.typeArguments.first()
            val genericTypeTempWriteFieldName = genericTypeDescriptor.getWritingTempFieldName(propertyDescriptor.fieldName)
            codeBlock.addStatement("$WRITER.beginArray()")
            codeBlock.beginControlFlow("for ($genericTypeTempWriteFieldName in $tempWritingFieldName)")
            addCheckCodeRecursively(
                codeBlock,
                propertyDescriptor,
                genericTypeDescriptor
            )
            codeBlock.endControlFlow()
            codeBlock.addStatement("$WRITER.endArray()")
        }

        val tempWritingFieldName = typeDescriptor.getWritingTempFieldName(propertyDescriptor.fieldName)
        if (propertyDescriptor.type == typeDescriptor) {
            codeBlock.addStatement(
                "val $tempWritingFieldName = $OBJECT.${propertyDescriptor.fieldName}"
            )
        }
        if (typeDescriptor.nullable()) {
            // if
            codeBlock.beginControlFlow("if ($tempWritingFieldName != null)")
            addWriteCollectionCode(codeBlock, propertyDescriptor, typeDescriptor)
            codeBlock.endControlFlow()
            // else
            codeBlock.beginControlFlow("else")
            codeBlock.addStatement("$WRITER.nullValue()")
            codeBlock.endControlFlow()
        } else {
            addWriteCollectionCode(codeBlock, propertyDescriptor, typeDescriptor)
        }
    }

    private fun addWriteNonCollectionTypeCode(
        codeBlock: FunSpec.Builder,
        propertyDescriptor: PropertyDescriptor,
        typeDescriptor: TypeDescriptor
    ) {
        val tempWritingFieldName = typeDescriptor.getWritingTempFieldName(propertyDescriptor.fieldName)
        if (typeDescriptor.nullable()) {
            codeBlock.beginControlFlow("if ($tempWritingFieldName != null)")
            realAddWriteNonCollectionTypeCode(codeBlock, propertyDescriptor, typeDescriptor)
            codeBlock.endControlFlow()

            codeBlock.beginControlFlow("else")
            codeBlock.addStatement("$WRITER.nullValue()")
            codeBlock.endControlFlow()
        } else {
            realAddWriteNonCollectionTypeCode(codeBlock, propertyDescriptor, typeDescriptor)
        }
    }

    private fun realAddWriteNonCollectionTypeCode(
        codeBlock: FunSpec.Builder,
        propertyDescriptor: PropertyDescriptor,
        typeDescriptor: TypeDescriptor
    ) {
        val tempWritingFieldName = typeDescriptor.getWritingTempFieldName(propertyDescriptor.fieldName)
        when {
            typeDescriptor.isPrimitive() -> {
                codeBlock.addStatement("$WRITER.value($tempWritingFieldName)")
            }
            typeDescriptor.isObject() -> {
                val typeAdapterFieldName = typeDescriptor.getTypeAdapterFieldName()
                codeBlock.addStatement("$typeAdapterFieldName.write($WRITER, $tempWritingFieldName)")
            }
            typeDescriptor.isEnum() -> {
                codeBlock.addStatement("$WRITER.value($tempWritingFieldName.name)")
            }
        }
    }
}