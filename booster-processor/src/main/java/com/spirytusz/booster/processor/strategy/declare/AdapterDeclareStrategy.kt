package com.spirytusz.booster.processor.strategy.declare

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import com.spirytusz.booster.processor.data.type.*

internal class AdapterDeclareStrategy : IAdapterDeclareStrategy {

    private val primitiveAdapterDeclareStrategy by lazy {
        PrimitiveAdapterDeclareStrategy()
    }

    private val objectAdapterDeclareStrategy by lazy {
        ObjectAdapterDeclareStrategy()
    }

    private val collectionAdapterDeclareStrategy by lazy {
        CollectionAdapterDeclareStrategy().apply {
            this.objectAdapterDeclareStrategy =
                this@AdapterDeclareStrategy.objectAdapterDeclareStrategy
            this.primitiveAdapterDeclareStrategy =
                this@AdapterDeclareStrategy.primitiveAdapterDeclareStrategy
        }
    }

    private val enumAdapterDeclareStrategy by lazy {
        EnumTypeAdapterDeclareStrategy()
    }

    private val backoffAdapterDeclareStrategy by lazy {
        BackoffAdapterDeclareStrategy()
    }

    fun setRegisterTypeAdapters(registerTypeAdapters: Map<String, ClassName>) {
        objectAdapterDeclareStrategy.registerTypeAdapters = registerTypeAdapters
    }

    override fun declare(kType: KType): PropertySpec? {
        return when (kType) {
            is PrimitiveKType -> {
                primitiveAdapterDeclareStrategy.declare(kType)
            }
            is CollectionKType -> {
                collectionAdapterDeclareStrategy.declare(kType)
            }
            is ObjectKType -> {
                objectAdapterDeclareStrategy.declare(kType)
            }
            is EnumKType -> {
                enumAdapterDeclareStrategy.declare(kType)
            }
            is BackoffKType -> {
                backoffAdapterDeclareStrategy.declare(kType)
            }
            else -> {
                null
            }
        }
    }
}