package com.spirytusz.booster.processor.ksp.provider

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.spirytusz.booster.processor.ksp.KspBoosterProcessor

class BoosterProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KspBoosterProcessor(environment)
    }
}