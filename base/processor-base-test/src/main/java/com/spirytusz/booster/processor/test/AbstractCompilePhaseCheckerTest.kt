package com.spirytusz.booster.processor.test

import com.spirytusz.booster.processor.test.extensions.fromResource
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Test

abstract class AbstractCompilePhaseCheckerTest {

    abstract val sourceCodePath: String

    abstract fun compile(sources: List<SourceFile>): KotlinCompilation.Result

    abstract fun checkResult(result: KotlinCompilation.Result)

    @Test
    fun test() {
        val source = SourceFile.fromResource(sourceCodePath)
        val sources = listOf(source)
        val result = compile(sources)

        checkResult(result)
    }
}