package com.spirytusz.booster.processor.gen

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.extensions.parameterizedBy
import com.spirytusz.booster.processor.base.gen.TypeAdapterClassGenerator
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.gen.const.Const.Naming.GSON
import com.spirytusz.booster.processor.gen.extensions.getTypeAdapterClassName
import com.spirytusz.booster.processor.gen.fields.FieldGenerator
import com.spirytusz.booster.processor.gen.functions.WriteFunctionGenerator
import com.spirytusz.booster.processor.gen.functions.read.ReadFunctionGenerator
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class TypeAdapterClassGeneratorImpl(private val logger: MessageLogger) : TypeAdapterClassGenerator {

    private val fieldGenerator by lazy { FieldGenerator(logger) }

    private val readFunctionGenerator by lazy { ReadFunctionGenerator(logger) }

    private val writeFunctionGenerator by lazy { WriteFunctionGenerator(logger) }

    override fun setClassFilter(classes: Set<KtType>) {
        val classFilterMap = classes.map {
            preCheckKtType(it, "setClassFilter() >>>")
            it.rawType to it
        }.toMap()
        fieldGenerator.setClassFilter(classFilterMap)
    }

    override fun generate(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig
    ): TypeSpec {
        preCheckKtType(scanner.classKtType, "generate() >>>")
        val className = scanner.classKtType
        val typeAdapterClassName = className.getTypeAdapterClassName()
        return TypeSpec.classBuilder(typeAdapterClassName)
            .superclass(TypeAdapter::class.parameterizedBy(className.asTypeName()))
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(GSON, Gson::class)
                    .build()
            ).addProperty(
                PropertySpec.builder(GSON, Gson::class)
                    .initializer(GSON)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            ).addClassBodyFields(scanner, config)
            .addReadFunction(scanner, config)
            .addWriteFunction(scanner, config)
            .build()
    }

    private fun TypeSpec.Builder.addClassBodyFields(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig
    ): TypeSpec.Builder = apply {
        scanner.ktFields.mapNotNull { ktField ->
            ktField.ktType.dfs { jsonTokenName.isObject() }.firstOrNull()
        }.distinctBy {
            it.rawType
        }.map {
            fieldGenerator.generateByKtType(scanner, it)
        }.forEach {
            addProperty(it)
        }
    }

    private fun TypeSpec.Builder.addReadFunction(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig
    ): TypeSpec.Builder = apply {
        addFunction(readFunctionGenerator.generate(scanner, config))
    }

    private fun TypeSpec.Builder.addWriteFunction(
        scanner: ClassScanner,
        config: TypeAdapterClassGenConfig
    ): TypeSpec.Builder = apply {
        addFunction(writeFunctionGenerator.generate(scanner, config))
    }

    private fun preCheckKtType(className: KtType, tag: String) {
        if (className.generics.isNotEmpty()) {
            logger.error("$tag illegal className ${className.toReadableString()}", className)
        }
    }
}