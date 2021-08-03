package com.zspirytus.booster.processor.strategy.declare

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import com.zspirytus.booster.processor.data.KField
import com.zspirytus.booster.processor.data.type.BackoffKType
import com.zspirytus.booster.processor.data.type.CollectionKType
import com.zspirytus.booster.processor.data.type.ObjectKType
import com.zspirytus.booster.processor.data.type.PrimitiveKType

internal class AdapterDeclareStrategy : IAdapterDeclareStrategy {

    private val primitiveAdapterDeclareStrategy by lazy {
        PrimitiveAdapterDeclareStrategy()
    }

    private val objectAdapterDeclareStrategy by lazy {
        ObjectAdapterDeclareStrategy()
    }

    private val collectionAdapterDeclareStrategy by lazy {
        CollectionAdapterDeclareStrategy()
    }

    private val backoffAdapterDeclareStrategy by lazy {
        BackoffAdapterDeclareStrategy()
    }

    fun setRegisterTypeAdapters(registerTypeAdapters: Map<String, ClassName>) {
        objectAdapterDeclareStrategy.registerTypeAdapters = registerTypeAdapters
    }

    override fun declare(kField: KField): PropertySpec? {
        return when (kField.kType) {
            is PrimitiveKType -> {
                primitiveAdapterDeclareStrategy.declare(kField)
            }
            is CollectionKType -> {
                collectionAdapterDeclareStrategy.declare(kField)
            }
            is ObjectKType -> {
                objectAdapterDeclareStrategy.declare(kField)
            }
            is BackoffKType -> {
                backoffAdapterDeclareStrategy.declare(kField)
            }
            else -> {
                null
            }
        }
    }
}