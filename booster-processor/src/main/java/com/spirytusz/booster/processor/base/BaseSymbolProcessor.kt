package com.spirytusz.booster.processor.base

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

abstract class BaseSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    protected val logger by lazy {
        environment.logger
    }
}