package com.spirytusz.booster.processor.kapt.test.base

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.spirytusz.booster.processor.base.const.Keys.KEY_TYPE_ADAPTER_FACTORY_NAME
import com.spirytusz.booster.processor.kapt.KaptBoosterProcessor
import com.spirytusz.booster.processor.test.AbstractSerializeTest
import com.spirytusz.booster.processor.test.extensions.getClassByName
import com.spirytusz.booster.processor.test.extensions.toJsonObject
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile

abstract class AbstractKaptSerializeTest : AbstractSerializeTest() {

    override fun compile(sources: List<SourceFile>): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            this.sources = sources
            inheritClassPath = true
            messageOutputStream = System.out
            annotationProcessors = listOf(KaptBoosterProcessor())
            kaptArgs =
                mutableMapOf(KEY_TYPE_ADAPTER_FACTORY_NAME to typeAdapterFactoryClassName)
        }.compile()
    }

    override fun getExpectJsonObject(result: KotlinCompilation.Result): JsonObject {
        val beanClass = result.getClassByName(beanClassName)
        return Gson().fromJson(json, beanClass).toJsonObject()
    }
}