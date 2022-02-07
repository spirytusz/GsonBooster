package com.spirytusz.booster.processor.ksp.test

import com.spirytusz.booster.processor.ksp.provider.BoosterProcessorProvider
import com.spirytusz.booster.processor.test.fromResource
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test

class KspBoosterProcessorTest {

    @Test
    fun test() {
        val source = SourceFile.fromResource("/com/spirytusz/booster/bean/Beans.kt")
        val sources = listOf(source)
        compile(sources)
    }

    private fun compile(sources: List<SourceFile>): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            this.sources = sources
            symbolProcessorProviders = listOf(BoosterProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
        }.compile()
    }

    private fun log(msg: String) {
        println("[BoosterTest] $msg")
    }
}