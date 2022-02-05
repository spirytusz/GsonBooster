package com.spirytusz.booster.processor.base.log

interface MessageLogger {

    fun debug(message: String, target: Any? = null)

    fun info(message: String, target: Any? = null)

    fun warn(message: String, target: Any? = null)

    fun error(message: String, target: Any? = null)
}