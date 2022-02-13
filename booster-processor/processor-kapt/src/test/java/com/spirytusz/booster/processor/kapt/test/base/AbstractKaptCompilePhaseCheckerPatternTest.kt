package com.spirytusz.booster.processor.kapt.test.base

import com.tschuchort.compiletesting.KotlinCompilation
import java.util.regex.Pattern

abstract class AbstractKaptCompilePhaseCheckerPatternTest : AbstractKaptCompilePhaseCheckerTest() {

    abstract val pattern: Pattern

    open val exitCode = KotlinCompilation.ExitCode.INTERNAL_ERROR

    override fun checkResult(result: KotlinCompilation.Result) {
        assert(result.exitCode == exitCode)

        assert(pattern.matcher(result.messages).find())
    }
}