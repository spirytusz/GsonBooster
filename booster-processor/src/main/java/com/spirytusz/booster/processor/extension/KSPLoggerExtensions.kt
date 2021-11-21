package com.spirytusz.booster.processor.extension

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

fun KSPLogger.info(tag: String, msg: String, symbol: KSNode? = null) {
    info("[$tag] $msg", symbol)
}

fun KSPLogger.warn(tag: String, msg: String, symbol: KSNode? = null) {
    warn("[$tag] $msg", symbol)
}

fun KSPLogger.error(tag: String, msg: String, symbol: KSNode? = null) {
    error("[$tag] $msg", symbol)
}