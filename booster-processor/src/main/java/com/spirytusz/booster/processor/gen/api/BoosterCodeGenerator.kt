package com.spirytusz.booster.processor.gen.api

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.spirytusz.booster.processor.gen.api.funcgen.read.BoosterReadFuncGenerator
import com.spirytusz.booster.processor.gen.api.funcgen.write.BoosterWriteFuncGenerator
import com.spirytusz.booster.processor.gen.api.propertygen.BoosterPropertyGenerator
import com.spirytusz.booster.processor.gen.const.Constants.GSON
import com.spirytusz.booster.processor.gen.extension.asTypeName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterFileName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterName
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

import com.squareup.kotlinpoet.ksp.writeTo

class BoosterCodeGenerator(
    private val environment: SymbolProcessorEnvironment,
    private val ksFile: KSFile,
    private val allClassScanners: Set<AbstractClassScanner>,
    private val ksFileClassScanners: Set<AbstractClassScanner>
) {

    private val propertyGenerator by lazy {
        BoosterPropertyGenerator(environment, ksFile, allClassScanners)
    }

    private val readFuncGenerator by lazy {
        BoosterReadFuncGenerator(ksFile)
    }

    private val writeFuncGenerator by lazy {
        BoosterWriteFuncGenerator(ksFile)
    }

    fun process() {
        val fileSpec = FileSpec.builder(
            packageName = ksFile.packageName.asString(),
            fileName = ksFile.getTypeAdapterFileName()
        ).apply {
            ksFileClassScanners.map {
                generateTypeAdapter(it)
            }.forEach {
                addType(it)
            }
        }.build()

        fileSpec.writeTo(codeGenerator = environment.codeGenerator, aggregating = false)
    }

    private fun generateTypeAdapter(classScanner: AbstractClassScanner): TypeSpec {
        val typeAdapterBuilder = TypeSpec
            .classBuilder(classScanner.getTypeAdapterName())
            .superclass(
                TypeAdapter::class.asClassName().parameterizedBy(classScanner.ksClass.asTypeName())
            ).primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(GSON, Gson::class.java)
                    .build()
            ).addProperty(
                PropertySpec.builder(GSON, Gson::class)
                    .initializer(GSON)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

        val propertySpecs = propertyGenerator.generateProperties(classScanner)

        typeAdapterBuilder.addProperties(propertySpecs)

        typeAdapterBuilder.addFunction(readFuncGenerator.generateReadFunc(classScanner))

        typeAdapterBuilder.addFunction(writeFuncGenerator.generateWriteFunc(classScanner))

        return typeAdapterBuilder.build()
    }

}