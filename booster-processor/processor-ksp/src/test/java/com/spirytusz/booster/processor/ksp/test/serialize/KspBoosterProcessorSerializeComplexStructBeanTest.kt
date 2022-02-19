package com.spirytusz.booster.processor.ksp.test.serialize

import com.spirytusz.booster.processor.ksp.test.base.AbstractKspSerializeTest

class KspBoosterProcessorSerializeComplexStructBeanTest : AbstractKspSerializeTest() {

    companion object {
        private const val TYPE_ADAPTER_FACTORY_CLASS_NAME =
            "com.spirytusz.booster.BoosterTypeAdapterFactory"
    }

    override val sourceCodePath: String = "/com/spirytusz/booster/bean/ComplexStructureBean.kt"

    override val beanClassName: String = "com.spirytusz.booster.bean.ComplexStructureBean"

    override val typeAdapterFactoryClassName: String = TYPE_ADAPTER_FACTORY_CLASS_NAME

    override val jsonFilePath: String = "/json/complex_struct_bean_result.json"
}