package com.spirytusz.booster.processor.gen.extension

import com.google.devtools.ksp.symbol.KSFile
import com.spirytusz.booster.processor.data.TypeDescriptor
import com.spirytusz.booster.processor.gen.const.Constants.TYPE_ADAPTER
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

fun AbstractClassScanner.getTypeAdapterName(): String {
    val ksClassName = this.ksClass.simpleName.asString()
    return "${ksClassName}$TYPE_ADAPTER"
}

fun TypeDescriptor.getTypeAdapterFieldName(): String {
    return "${joinWithArguments()}$TYPE_ADAPTER".firstCharToLowerCase()
}

fun KSFile.getTypeAdapterFileName(): String {
    val fileNameWithSuffix = fileName
    val fileName = fileNameWithSuffix.replace(".kt", "").replace(".java", "")
    return "$fileName$TYPE_ADAPTER"
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