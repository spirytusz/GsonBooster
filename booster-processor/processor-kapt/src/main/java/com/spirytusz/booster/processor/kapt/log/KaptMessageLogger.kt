package com.spirytusz.booster.processor.kapt.log

import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.scan.kapt.data.IElementOwner
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class KaptMessageLogger(private val processingEnvironment: ProcessingEnvironment) : MessageLogger {

    companion object {
        private const val TAG = "[Booster]"
    }

    override fun debug(message: String, target: Any?) {
        processingEnvironment.messager.printMessage(
            Diagnostic.Kind.OTHER,
            "$TAG $message",
            target?.tryCastElement()
        )
    }

    override fun info(message: String, target: Any?) {
        processingEnvironment.messager.printMessage(
            Diagnostic.Kind.NOTE,
            "$TAG $message",
            target?.tryCastElement()
        )
    }

    override fun warn(message: String, target: Any?) {
        processingEnvironment.messager.printMessage(
            Diagnostic.Kind.WARNING,
            "$TAG $message",
            target?.tryCastElement()
        )
    }

    override fun error(message: String, target: Any?) {
        processingEnvironment.messager.printMessage(
            Diagnostic.Kind.ERROR,
            "$TAG $message",
            target?.tryCastElement()
        )
    }

    private fun Any.tryCastElement(): Element? {
        return when (this) {
            is IElementOwner -> this.target
            is Element -> this
            else -> null
        }
    }
}