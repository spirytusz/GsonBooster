package com.spirytusz.booster.processor.strategy.declare

import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.spirytusz.booster.processor.const.GSON
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.extensions.kotlinType
import com.squareup.kotlinpoet.*

/**
 * Input:  [KType] = Foo<Int>
 *
 * Output: private val fooIntTypeAdapter by lazy { gson.getAdapter(object: TypeToken<Foo<Int>>() {}) }
 */
class BackoffAdapterDeclareStrategy : IAdapterDeclareStrategy {
    override fun declare(kType: KType): PropertySpec? {
        val typeAdapterCodeBlock = CodeBlock.Builder()
            .beginControlFlow("lazy")
            .addStatement(
                "$GSON.getAdapter(object: %T<%L>() {})",
                TypeToken::class.java,
                kType.typeName.kotlinType()
            )
            .endControlFlow()
            .build()
        val adapterType = with(ParameterizedTypeName.Companion) {
            TypeAdapter::class.asClassName().parameterizedBy(kType.typeName.kotlinType())
        }
        return PropertySpec.builder(kType.adapterFieldName, adapterType, KModifier.PRIVATE)
            .delegate(typeAdapterCodeBlock)
            .build()
    }
}