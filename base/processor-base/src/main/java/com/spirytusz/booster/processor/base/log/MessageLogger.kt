package com.spirytusz.booster.processor.base.log

interface MessageLogger {

    fun debug(message: String) = debug(message, null)

    fun debug(message: String, target: Any? = null)

    fun info(message: String) = info(message, null)

    fun info(message: String, target: Any? = null)

    fun warn(message: String) = warn(message, null)

    fun warn(message: String, target: Any? = null)

    fun error(message: String) = error(message, null)

    fun error(message: String, target: Any? = null)
}