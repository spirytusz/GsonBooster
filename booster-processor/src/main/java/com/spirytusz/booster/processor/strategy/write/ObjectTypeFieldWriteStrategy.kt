package com.spirytusz.booster.processor.strategy.write

import com.spirytusz.booster.processor.const.OBJECT
import com.spirytusz.booster.processor.const.WRITER
import com.spirytusz.booster.processor.data.KField
import com.squareup.kotlinpoet.CodeBlock

/**
 * Input: [KField] = val foo: Foo = Foo()
 *
 * Output:
 * fooTypeAdapter.write(writer, foo)
 */
class ObjectTypeFieldWriteStrategy : IFieldWriteStrategy {
    override fun write(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        codeBlock.addStatement("$WRITER.name(%S)", kField.keys.first())
        if (kField.nullable) {
            codeBlock.addStatement(
                "val %L = $OBJECT.%L",
                kField.fieldName,
                kField.fieldName
            )
            codeBlock.addStatement(
                """
                    if (%L != null) {
                        %L.write($WRITER, %L)
                    } else {
                        $WRITER.nullValue()
                    }
                """.trimIndent(),
                kField.fieldName,
                kField.kType.adapterFieldName,
                kField.fieldName
            )
        } else {
            codeBlock.addStatement(
                "%L.write($WRITER, $OBJECT.%L)",
                kField.kType.adapterFieldName,
                kField.fieldName
            )
        }
        return codeBlock.build()
    }
}