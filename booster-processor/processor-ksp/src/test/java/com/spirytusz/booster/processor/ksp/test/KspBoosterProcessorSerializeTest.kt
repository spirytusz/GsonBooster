package com.spirytusz.booster.processor.ksp.test

import com.spirytusz.booster.processor.ksp.test.base.AbstractKspSerializeTest

class KspBoosterProcessorSerializeTest : AbstractKspSerializeTest() {

    companion object {
        private const val TYPE_ADAPTER_FACTORY_CLASS_NAME =
            "com.spirytusz.booster.BoosterTypeAdapterFactory"
    }

    override val sourceCodePath: String = "/com/spirytusz/booster/bean/Beans.kt"

    override val beanClassName: String = "com.spirytusz.booster.bean.Bean"

    override val typeAdapterFactoryClassName: String = TYPE_ADAPTER_FACTORY_CLASS_NAME

    override val jsonFilePath: String = "/json/bean_result.json"
}