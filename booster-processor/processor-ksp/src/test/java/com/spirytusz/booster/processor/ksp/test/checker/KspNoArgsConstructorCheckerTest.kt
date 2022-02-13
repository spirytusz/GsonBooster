package com.spirytusz.booster.processor.ksp.test.checker

import com.spirytusz.booster.processor.ksp.test.base.AbstractKspCompilePhaseCheckerPatternTest
import java.util.regex.Pattern

class KspNoArgsConstructorCheckerTest : AbstractKspCompilePhaseCheckerPatternTest() {
    override val sourceCodePath: String = "/com/spirytusz/booster/bean/ClassWithArgsConstructor.kt"

    override val pattern: Pattern =
        Pattern.compile("WithoutNoArgsConstructorException: .* \\[.*ClassWithArgsConstructor]")
}