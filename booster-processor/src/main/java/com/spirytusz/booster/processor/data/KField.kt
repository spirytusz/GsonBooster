package com.spirytusz.booster.processor.data

import com.spirytusz.booster.processor.const.NULLABLE_FIELD_FLAG_PREFIX
import com.spirytusz.booster.processor.const.NULLABLE_FIELD_REAL_FIELD_NAME_PREFIX
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.extensions.firstChatUpperCase

/**
 * 代表一个字段
 *
 * @param keys      反序列化时的key集合
 * @param kType     字段类型
 * @param fieldName 字段名
 * @param nullable  是否可空
 * @param isFinal   是否不可变
 */
data class KField(
    val keys: Set<String>,
    val kType: KType,
    val fieldName: String,
    val nullable: Boolean,
    val isFinal: Boolean
) {

    /**
     * 当这个字段可空的时候，需要有一个标记来标记json字符串中是否有这个字段的key
     * 这个标记字段的名字就是[fetchFlagFieldName]
     */
    val fetchFlagFieldName: String by lazy {
        "$NULLABLE_FIELD_FLAG_PREFIX${fieldName.firstChatUpperCase()}"
    }

    /**
     * 当这个字段可空的时候，需要处理
     * 1. json字符串有这个字段的key
     * 2. json字符串没有这个字段的key
     * 这两种情况
     *
     * 结果保存在一个临时字段，这个临时字段的名字就是[nullableFieldRealFieldName]
     */
    val nullableFieldRealFieldName: String by lazy {
        "$NULLABLE_FIELD_REAL_FIELD_NAME_PREFIX${fieldName.firstChatUpperCase()}"
    }
}