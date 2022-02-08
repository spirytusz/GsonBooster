package com.spirytusz.booster.processor.gen.functions

import com.google.gson.stream.JsonReader
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.gen.const.Const.Naming.READER
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

class ReadFunctionGenerator(private val logger: MessageLogger) {

    fun generate(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig
    ): FunSpec {
        val className = scanner.classKtType.asTypeName() as ClassName
        val readFunc = FunSpec.builder("read")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(READER, JsonReader::class)
            .returns(className.copy(nullable = true))
        readFunc.addStatement("return null")
        return readFunc.build()
    }
}