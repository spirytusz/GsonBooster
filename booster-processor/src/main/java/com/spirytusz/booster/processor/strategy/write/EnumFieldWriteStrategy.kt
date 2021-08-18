package com.spirytusz.booster.processor.strategy.write

import com.spirytusz.booster.processor.const.OBJECT
import com.spirytusz.booster.processor.const.WRITER
import com.spirytusz.booster.processor.data.KField
import com.squareup.kotlinpoet.CodeBlock

/**
 * Input: [KField] = val testEnum: TestEnum = TestEnum.HI
 *
 * Output:
 * writer.value(testEnum.name)
 */
class EnumFieldWriteStrategy : IFieldWriteStrategy {
    override fun write(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        codeBlock.addStatement("$WRITER.name(%S)", kField.keys.first())
        if (kField.nullable) {
            codeBlock.addStatement(
                "val %L = $OBJECT.%L?.name",
                kField.fieldName,
                kField.fieldName
            )
            codeBlock.addStatement(
                """
                if (%L != null) {
                    $WRITER.value(%L)
                } else {
                    $WRITER.nullValue()
                }
                """.trimIndent(),
                kField.fieldName,
                kField.fieldName
            )
        } else {
            codeBlock.addStatement("$WRITER.value($OBJECT.%L.name)", kField.fieldName)
        }
        return codeBlock.build()
    }
}