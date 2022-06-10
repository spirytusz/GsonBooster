package com.spirytusz.booster.processor.ksp.test.base

import com.spirytusz.booster.processor.ksp.provider.BoosterProcessorProvider
import com.spirytusz.booster.processor.test.AbstractCompilePhaseCheckerTest
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders

abstract class AbstractKspCompilePhaseCheckerTest : AbstractCompilePhaseCheckerTest() {
    override fun compile(sources: List<SourceFile>): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            this.sources = sources
            inheritClassPath = true
            messageOutputStream = System.out
            symbolProcessorProviders = listOf(BoosterProcessorProvider())
        }.compile()
    }
}