package com.spirytusz.booster.processor.ksp.log

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.scan.ksp.data.IKsNodeOwner

class KspMessageLogger(private val environment: SymbolProcessorEnvironment) : MessageLogger {

    companion object {
        private const val TAG = "[Booster]"
    }

    override fun debug(message: String, target: Any?) {
    }

    override fun info(message: String, target: Any?) {
        environment.logger.info("$TAG $message", (target as? IKsNodeOwner)?.target)
    }

    override fun warn(message: String, target: Any?) {
        environment.logger.warn("$TAG $message", (target as? IKsNodeOwner)?.target)
    }

    override fun error(message: String, target: Any?) {
        environment.logger.error("$TAG $message", (target as? IKsNodeOwner)?.target)
    }
}