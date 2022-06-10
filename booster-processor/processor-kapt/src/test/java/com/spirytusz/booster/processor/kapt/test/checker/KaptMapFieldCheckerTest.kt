package com.spirytusz.booster.processor.kapt.test.checker

import com.spirytusz.booster.processor.kapt.test.base.AbstractKaptCompilePhaseCheckerPatternTest
import java.util.regex.Pattern

class KaptMapFieldCheckerTest : AbstractKaptCompilePhaseCheckerPatternTest() {
    override val sourceCodePath: String = "/com/spirytusz/booster/bean/ClassWithMapIntAny.kt"

    override val pattern: Pattern =
        Pattern.compile("InvalidMapKeyGenericsException: .* \\[.*ClassWithMapIntAny]")
}