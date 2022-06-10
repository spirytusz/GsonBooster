package com.spirytusz.booster.processor.ksp.test.checker

import com.spirytusz.booster.processor.ksp.test.base.AbstractKspCompilePhaseCheckerPatternTest
import java.util.regex.Pattern

class KspGetterSetterVisibilityCheckerTest : AbstractKspCompilePhaseCheckerPatternTest() {
    override val sourceCodePath: String =
        "/com/spirytusz/booster/bean/ClassWithInvisibleGetterSetterFields.kt"

    override val pattern: Pattern =
        Pattern.compile("InvisibleGetterSetterException: .* \\[.*ClassWithInvisibleGetterSetterFields]")
}