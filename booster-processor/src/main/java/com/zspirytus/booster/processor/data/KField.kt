package com.zspirytus.booster.processor.data

import com.zspirytus.booster.processor.const.NULLABLE_FIELD_FLAG_PREFIX
import com.zspirytus.booster.processor.data.type.KType
import com.zspirytus.booster.processor.extensions.firstChatUpperCase

internal data class KField(
    val keys: Set<String>,
    val kType: KType,
    val fieldName: String,
    val nullable: Boolean
) {

    val fetchFlagFieldName: String by lazy {
        "$NULLABLE_FIELD_FLAG_PREFIX${fieldName.firstChatUpperCase()}"
    }
}