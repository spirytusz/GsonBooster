package com.spirytusz.booster.processor.strategy.read

import com.google.gson.stream.JsonToken
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.data.KField
import com.squareup.kotlinpoet.CodeBlock

/**
 * Input: [KField] = val foo: Foo = Foo()
 *
 * Output:
 * val peeked = reader.peek()
 * if (peeked == JsonToken.BEGIN_OBJECT) {
 *     foo = fooTypeAdapter.read(reader)
 * } else if (peeked == JsonToken.NULL) {
 *     reader.nextNull()
 * } else {
 *     reader.skipValue()
 * }
 */
class ObjectFieldReadStrategy : IFieldReadStrategy {
    override fun read(kField: KField): CodeBlock {
        val codeBlock = CodeBlock.Builder()
        val fetchFlagStatement = "${kField.fetchFlagFieldName} = true"
        codeBlock.addStatement(
            """
            val peeked = ${READER}.peek()
            if (peeked == %T.%L) {
                %L = %L.read(${READER})
                ${if (kField.nullable) fetchFlagStatement else ""}
            } else if (peeked == %T.NULL) {
                ${READER}.nextNull()
                ${if (kField.nullable) fetchFlagStatement else ""}
            } else {
                ${READER}.skipValue()
            }
            """.trimIndent(),
            JsonToken::class.java,
            kField.kType.jsonTokenName,
            kField.fieldName,
            kField.kType.adapterFieldName,
            JsonToken::class.java
        )
        return codeBlock.build()
    }
}