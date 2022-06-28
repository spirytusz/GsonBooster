package com.spirytusz.booster.processor.gen.fields

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.contract.Constants.Naming.GSON
import com.spirytusz.booster.processor.gen.extensions.getTypeAdapterClassName
import com.spirytusz.booster.processor.gen.extensions.getTypeAdapterFieldName
import com.squareup.kotlinpoet.*

internal class FieldGenerator(private val logger: MessageLogger) {

    private lateinit var classFilterMap: Map<String, KtType>

    fun setClassFilter(classFilterMap: Map<String, KtType>) {
        this.classFilterMap = classFilterMap
    }

    fun generateByKtType(scanner: ClassScanner, ktType: KtType): PropertySpec {
        val isRegisteredType =
            classFilterMap.containsKey(ktType.rawType) && ktType.generics.isEmpty()
        val adapterFieldName = ktType.getTypeAdapterFieldName()
        val typeName = ktType.asTypeName(ignoreVariance = true, ignoreNullability = true)
        val typeAdapterCodeBlock = if (isRegisteredType) {
            // 已经注册的，使用XXXTypeAdapter()
            val build = CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement("%T($GSON)", ktType.getTypeAdapterClassName())
                .endControlFlow()
                .build()
            build
        } else {
            // 没有注册的，使用gson.getAdapter(XXX::class.java)
            CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement("$GSON.getAdapter(object : %T<%T>() {})", TypeToken::class, typeName)
                .endControlFlow()
                .build()
        }
        val adapterType = with(ParameterizedTypeName.Companion) {
            TypeAdapter::class.asClassName().parameterizedBy(typeName)
        }
        logger.debug(
            "generate field $adapterFieldName for class ${scanner.classKtType.asTypeName()}"
        )
        return PropertySpec.builder(adapterFieldName, adapterType, KModifier.PRIVATE)
            .delegate(typeAdapterCodeBlock)
            .build()
    }
}