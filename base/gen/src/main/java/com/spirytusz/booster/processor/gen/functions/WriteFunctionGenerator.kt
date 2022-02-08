package com.spirytusz.booster.processor.gen.functions

import com.google.gson.stream.JsonWriter
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.gen.const.Const.Naming.OBJECT
import com.spirytusz.booster.processor.gen.const.Const.Naming.WRITER
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

class WriteFunctionGenerator(private val logger: MessageLogger) {

    fun generate(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig
    ): FunSpec {
        val className = scanner.classKtType.asTypeName() as ClassName
        val writeFunc = FunSpec.builder("write")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(WRITER, JsonWriter::class)
            .addParameter(OBJECT, className.copy(nullable = true))
        return writeFunc.build()
    }
}