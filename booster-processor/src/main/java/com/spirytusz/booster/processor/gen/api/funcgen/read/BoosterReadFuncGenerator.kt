package com.spirytusz.booster.processor.gen.api.funcgen.read

import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.stream.JsonReader
import com.spirytusz.booster.processor.gen.const.Constants.READER
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

import com.squareup.kotlinpoet.ksp.toClassName

class BoosterReadFuncGenerator(
    private val ksFile: KSFile
) {

    fun generateReadFunc(classScanner: AbstractClassScanner): FunSpec {
        val readFunc = FunSpec.builder("read")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(READER, JsonReader::class.java)
            .returns(classScanner.ksClass.toClassName().copy(nullable = true))
        readFunc.addCode("return null")
        return readFunc.build()
    }
}