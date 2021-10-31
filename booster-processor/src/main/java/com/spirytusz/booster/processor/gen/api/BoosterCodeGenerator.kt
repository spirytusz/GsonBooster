package com.spirytusz.booster.processor.gen.api

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.spirytusz.booster.processor.gen.api.propertygen.BoosterPropertyGenerator
import com.spirytusz.booster.processor.gen.const.Constants.GSON
import com.spirytusz.booster.processor.gen.extension.asTypeName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterName
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

class BoosterCodeGenerator(
    private val environment: SymbolProcessorEnvironment,
    private val ksFile: KSFile,
    private val allClassScanners: Set<AbstractClassScanner>,
    private val ksFileClassScanners: Set<AbstractClassScanner>
) {

    @KotlinPoetKspPreview
    fun process() {
        ksFileClassScanners.first().apply {
            environment.logger.warn(generateTypeAdapter(this).toString())
        }
    }

    @KotlinPoetKspPreview
    private fun generateTypeAdapter(classScanner: AbstractClassScanner): TypeSpec {
        val typeAdapterBuilder = TypeSpec
            .classBuilder(classScanner.getTypeAdapterName())
            .superclass(
                TypeAdapter::class.asClassName().parameterizedBy(classScanner.ksClass.asTypeName())
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(GSON, Gson::class.java)
                    .build()
            ).addProperty(
                PropertySpec.builder(GSON, Gson::class)
                    .initializer(GSON)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

        val propertySpecs = BoosterPropertyGenerator(
            ksFile = ksFile,
            classScanner = classScanner,
            allClassScanners = allClassScanners
        ).generateProperties()
        typeAdapterBuilder.addProperties(propertySpecs)


        return typeAdapterBuilder.build()
    }

}