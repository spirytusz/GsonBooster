package com.spirytusz.booster.processor.data

import com.google.devtools.ksp.symbol.KSNode
import com.google.gson.annotations.SerializedName

/**
 * 代表一个变量
 *
 * @param keys [SerializedName]的字面值
 * @param mutable 是否可变
 * @param fieldName 变量名
 * @param hasDefault 是否有默认值
 * @param transient 是否transient的
 * @param type 变量类型
 */
data class PropertyDescriptor(
    val keys: Set<String>,
    val mutable: Boolean,
    val fieldName: String,
    val hasDefault: Boolean,
    val transient: Boolean,
    val type: TypeDescriptor,
    val origin: KSNode
) {
    override fun toString(): String = buildString {
        if (transient) {
            append("transient ")
        }
        if (mutable) {
            append("var ")
        } else {
            append("val ")
        }
        append("$fieldName: ")
        append(type.flattenCanonical())
        if (hasDefault) {
            append(" hasDefault")
        }
        append(" keys: $keys")
        append(" JsonToken: ${type.jsonTokenName.name}")
    }
}