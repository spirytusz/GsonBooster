package com.spirytusz.booster.processor.gen.api.funcgen.write

import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.stream.JsonWriter
import com.spirytusz.booster.processor.gen.const.Constants.OBJECT
import com.spirytusz.booster.processor.gen.const.Constants.WRITER
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

import com.squareup.kotlinpoet.ksp.toClassName

class BoosterWriteFuncGenerator(
    private val ksFile: KSFile
) {


    fun generateWriteFunc(classScanner: AbstractClassScanner): FunSpec {
        val writeFunc = FunSpec.builder("write")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(WRITER, JsonWriter::class)
            .addParameter(OBJECT, classScanner.ksClass.toClassName().copy(nullable = true))
        return writeFunc.build()
    }
}