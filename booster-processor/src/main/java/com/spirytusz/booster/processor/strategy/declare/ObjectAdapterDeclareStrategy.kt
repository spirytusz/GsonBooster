package com.spirytusz.booster.processor.strategy.declare

import com.google.gson.TypeAdapter
import com.spirytusz.booster.processor.const.GSON
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.extensions.toTypeAdapterClassName
import com.squareup.kotlinpoet.*

/**
 * Input:  [KType] = Foo
 *
 * Output: private val fooTypeAdapter by lazy { FooTypeAdapter() }
 */
class ObjectAdapterDeclareStrategy : IAdapterDeclareStrategy {

    var registerTypeAdapters = mapOf<String, ClassName>()

    override fun declare(kType: KType): PropertySpec? {
        val typeClassName = kType.typeName as ClassName
        val isRegisteredType = registerTypeAdapters.containsKey(typeClassName.canonicalName)
        val type = if (isRegisteredType) {
            registerTypeAdapters[typeClassName.canonicalName]
        } else {
            kType.typeName
        }
        val typeAdapterCodeBlock = if (isRegisteredType) {
            // 已经注册的，使用XXXTypeAdapter()
            CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement("%T($GSON)", (type as ClassName).toTypeAdapterClassName())
                .endControlFlow()
                .build()
        } else {
            // 没有注册的，使用gson.getAdapter(XXX::class.java)
            CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement("$GSON.getAdapter(%T::class.java)", type)
                .endControlFlow()
                .build()
        }
        val adapterType = with(ParameterizedTypeName.Companion) {
            TypeAdapter::class.asClassName().parameterizedBy(kType.typeName)
        }
        return PropertySpec.builder(kType.adapterFieldName, adapterType, KModifier.PRIVATE)
            .delegate(typeAdapterCodeBlock)
            .build()
    }
}