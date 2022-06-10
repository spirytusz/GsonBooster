package com.spirytusz.booster.processor.kapt.test.checker

import com.spirytusz.booster.processor.kapt.test.base.AbstractKaptCompilePhaseCheckerPatternTest
import java.util.regex.Pattern

class KaptNoArgsConstructorCheckerTest : AbstractKaptCompilePhaseCheckerPatternTest() {
    override val sourceCodePath: String = "/com/spirytusz/booster/bean/ClassWithArgsConstructor.kt"

    override val pattern: Pattern =
        Pattern.compile("WithoutNoArgsConstructorException: .* \\[.*ClassWithArgsConstructor]")
}