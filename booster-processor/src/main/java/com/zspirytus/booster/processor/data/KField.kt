package com.zspirytus.booster.processor.data

import com.zspirytus.booster.processor.data.type.KType

internal data class KField(
    val keys: Set<String>,
    val kType: KType,
    val fieldName: String,
    val nullable: Boolean
)