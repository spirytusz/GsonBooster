package com.spirytusz.booster.processor.ksp.test.checker

import com.spirytusz.booster.processor.ksp.test.base.AbstractKspCompilePhaseCheckerPatternTest
import com.tschuchort.compiletesting.KotlinCompilation
import java.util.regex.Pattern

class KspTypeVarianceCheckerTest : AbstractKspCompilePhaseCheckerPatternTest() {
    override val sourceCodePath: String =
        "/com/spirytusz/booster/bean/ClassWithVariantTypeFields.kt"

    override val exitCode: KotlinCompilation.ExitCode = KotlinCompilation.ExitCode.OK

    override val pattern: Pattern =
        Pattern.compile("not recommend variant type")
}