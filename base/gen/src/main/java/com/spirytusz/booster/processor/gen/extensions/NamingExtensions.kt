package com.spirytusz.booster.processor.gen.extensions

import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.gen.const.Const.Naming.TEMP_FIELD_PREFIX
import com.spirytusz.booster.processor.gen.const.Const.Naming.TYPE_ADAPTER
import com.squareup.kotlinpoet.ClassName

fun String.firstCharLowerCase() =
    this.replaceFirst(this.first(), this.first().toLowerCase())

fun String.firstChatUpperCase() =
    this.replaceFirst(this.first(), this.first().toUpperCase())

fun KtType.flatten(): String = buildString {
    append(ClassName.bestGuess(rawType).simpleName)
    generics.forEach { append(it.flatten()) }
}

fun KtType.getTypeAdapterFieldName(): String {
    return "${flatten()}$TYPE_ADAPTER".firstCharLowerCase()
}

fun KtType.getTypeAdapterClassName(): ClassName {
    return ClassName.bestGuess("$rawType$TYPE_ADAPTER")
}

fun KtType.getReadingTempFieldName(): String {
    return "${TEMP_FIELD_PREFIX}Reading${flatten()}"
}

fun KtType.getWritingTempFieldName(fieldName: String): String {
    return "${TEMP_FIELD_PREFIX}Writing${flatten()}${fieldName.firstChatUpperCase()}"
}