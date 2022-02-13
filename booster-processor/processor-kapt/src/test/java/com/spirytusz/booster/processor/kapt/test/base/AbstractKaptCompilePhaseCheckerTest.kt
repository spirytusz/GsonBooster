package com.spirytusz.booster.processor.kapt.test.base

import com.spirytusz.booster.processor.kapt.KaptBoosterProcessor
import com.spirytusz.booster.processor.test.AbstractCompilePhaseCheckerTest
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile

abstract class AbstractKaptCompilePhaseCheckerTest : AbstractCompilePhaseCheckerTest() {
    override fun compile(sources: List<SourceFile>): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            this.sources = sources
            inheritClassPath = true
            messageOutputStream = System.out
            annotationProcessors = listOf(KaptBoosterProcessor())
        }.compile()
    }
}