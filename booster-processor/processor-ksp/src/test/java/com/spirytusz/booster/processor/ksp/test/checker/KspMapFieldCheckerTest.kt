package com.spirytusz.booster.processor.ksp.test.checker

import com.spirytusz.booster.processor.ksp.test.base.AbstractKspCompilePhaseCheckerPatternTest
import java.util.regex.Pattern

class KspMapFieldCheckerTest : AbstractKspCompilePhaseCheckerPatternTest() {
    override val sourceCodePath: String = "/com/spirytusz/booster/bean/ClassWithMapIntAny.kt"

    override val pattern: Pattern =
        Pattern.compile("InvalidMapKeyGenericsException: .* \\[.*ClassWithMapIntAny]")
}