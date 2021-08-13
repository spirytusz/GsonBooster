package com.spirytusz.booster.processor.data

import com.spirytusz.booster.processor.const.NULLABLE_FIELD_FLAG_PREFIX
import com.spirytusz.booster.processor.const.NULLABLE_FIELD_REAL_FIELD_NAME_PREFIX
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.extensions.firstChatUpperCase

internal data class KField(
    val keys: Set<String>,
    val kType: KType,
    val fieldName: String,
    val nullable: Boolean,
    val isFinal: Boolean
) {

    val fetchFlagFieldName: String by lazy {
        "$NULLABLE_FIELD_FLAG_PREFIX${fieldName.firstChatUpperCase()}"
    }

    val nullableFieldRealFieldName: String by lazy {
        "$NULLABLE_FIELD_REAL_FIELD_NAME_PREFIX${fieldName.firstChatUpperCase()}"
    }
}