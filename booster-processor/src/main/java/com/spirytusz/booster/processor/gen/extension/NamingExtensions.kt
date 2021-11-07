package com.spirytusz.booster.processor.gen.extension

import com.google.devtools.ksp.symbol.KSFile
import com.spirytusz.booster.processor.data.TypeDescriptor
import com.spirytusz.booster.processor.gen.const.Constants.READING_STORE_TEMP_FIELD_NAME_PREFIX
import com.spirytusz.booster.processor.gen.const.Constants.TYPE_ADAPTER
import com.spirytusz.booster.processor.gen.const.Constants.WRITING_TEMP_FIELD_NAME_PREFIX
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.ClassName

fun AbstractClassScanner.getTypeAdapterName(): String {
    val ksClassName = this.ksClass.simpleName.asString()
    return "${ksClassName}$TYPE_ADAPTER"
}

fun AbstractClassScanner.getTypeAdapterClassName(): ClassName {
    val typeAdapterName = getTypeAdapterName()
    val split = typeAdapterName.split(".")
    val packageName = split.subList(0, split.size - 1).joinToString(separator = ".") { it }
    val simpleName = split.last()
    return ClassName(packageName, simpleName)
}

fun TypeDescriptor.getTypeAdapterName(): String {
    return "${flattenSimple()}$TYPE_ADAPTER"
}

fun TypeDescriptor.getTypeAdapterFieldName(): String {
    return "${joinWithArguments()}$TYPE_ADAPTER".firstCharToLowerCase()
}

fun KSFile.getTypeAdapterFileName(): String {
    val fileNameWithSuffix = fileName
    val fileName = fileNameWithSuffix.replace(".kt", "").replace(".java", "")
    return "$fileName$TYPE_ADAPTER"
}

fun TypeDescriptor.getReadingStoreTempFieldName(): String {
    return "$READING_STORE_TEMP_FIELD_NAME_PREFIX${joinWithArguments().firstCharToUpperCase()}"
}

fun TypeDescriptor.getWritingTempFieldName(): String {
    return "$WRITING_TEMP_FIELD_NAME_PREFIX${joinWithArguments().firstCharToUpperCase()}"
}

private fun TypeDescriptor.joinWithArguments(): String = buildString {
    append(raw.split(".").last())
    typeArguments.forEach {
        append(it.joinWithArguments())
    }
}

private fun String.firstCharToLowerCase(): String {
    return this.replaceFirstChar { it.lowercaseChar() }
}

private fun String.firstCharToUpperCase(): String {
    return this.replaceFirstChar { it.uppercaseChar() }
}