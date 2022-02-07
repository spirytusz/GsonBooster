package com.spirytusz.booster.processor.kapt.test

import com.spirytusz.booster.processor.kapt.KaptBoosterProcessor
import com.spirytusz.booster.processor.test.fromResource
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Test

class KaptBoosterProcessorTest {

    @Test
    fun test() {
        val source = SourceFile.fromResource("/com/spirytusz/booster/bean/Beans.kt")
        val sources = listOf(source)
        compile(sources)
    }

    private fun compile(sources: List<SourceFile>): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            this.sources = sources
            annotationProcessors = listOf(KaptBoosterProcessor())
            inheritClassPath = true
            messageOutputStream = System.out
        }.compile()
    }

    private fun log(msg: String) {
        println("[BoosterTest] $msg")
    }
}