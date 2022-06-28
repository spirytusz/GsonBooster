package com.spirytusz.booster.processor.ksp.test.base

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.spirytusz.booster.contract.Constants.BoosterKeys.KEY_TYPE_ADAPTER_FACTORY_NAME
import com.spirytusz.booster.processor.ksp.provider.BoosterProcessorProvider
import com.spirytusz.booster.processor.ksp.test.extensions.kspGeneratedSources
import com.spirytusz.booster.processor.test.AbstractSerializeTest
import com.spirytusz.booster.processor.test.extensions.getClassByName
import com.spirytusz.booster.processor.test.extensions.toJsonObject
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.symbolProcessorProviders

abstract class AbstractKspSerializeTest : AbstractSerializeTest() {

    override fun compile(sources: List<SourceFile>): KotlinCompilation.Result {
        printLog("compile beans")
        val compileBeanResult = KotlinCompilation().apply {
            this.sources = sources
            inheritClassPath = true
            messageOutputStream = System.out
            symbolProcessorProviders = listOf(BoosterProcessorProvider())
            kspArgs =
                mutableMapOf(KEY_TYPE_ADAPTER_FACTORY_NAME to typeAdapterFactoryClassName)
        }.compile()
        val kspGenerateSources = compileBeanResult.kspGeneratedSources.map {
            SourceFile.fromPath(it)
        }
        printLog("compile again with kspGenerateSources=$kspGenerateSources")
        return KotlinCompilation().apply {
            this.sources = sources + kspGenerateSources
            inheritClassPath = true
            messageOutputStream = System.out
        }.compile()
    }

    override fun getExpectJsonObject(result: KotlinCompilation.Result): JsonObject {
        val beanClass = result.getClassByName(beanClassName)
        return Gson().fromJson(json, beanClass).toJsonObject()
    }
}