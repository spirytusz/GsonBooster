package com.spirytusz.booster.processor.kapt.test.serialize

import com.spirytusz.booster.processor.kapt.test.base.AbstractKaptSerializeTest

class KaptBoosterProcessorSerializeTest : AbstractKaptSerializeTest() {

    companion object {
        private const val TYPE_ADAPTER_FACTORY_CLASS_NAME =
            "com.spirytusz.booster.BoosterTypeAdapterFactory"
    }

    override val sourceCodePath: String = "/com/spirytusz/booster/bean/Beans.kt"

    override val beanClassName: String = "com.spirytusz.booster.bean.Bean"

    override val typeAdapterFactoryClassName: String = TYPE_ADAPTER_FACTORY_CLASS_NAME

    override val jsonFilePath: String = "/json/bean_result.json"
}