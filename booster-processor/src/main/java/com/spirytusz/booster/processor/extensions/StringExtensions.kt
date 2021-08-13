package com.spirytusz.booster.processor.extensions

fun String.firstCharLowerCase() =
    this.replaceFirst(this.first(), this.first().toLowerCase())

fun String.firstChatUpperCase() =
    this.replaceFirst(this.first(), this.first().toUpperCase())