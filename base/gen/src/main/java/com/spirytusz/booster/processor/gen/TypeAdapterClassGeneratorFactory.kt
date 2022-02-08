package com.spirytusz.booster.processor.gen

import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger

object TypeAdapterClassGeneratorFactory {

    fun create(classFilter: Set<KtType>, logger: MessageLogger) =
        TypeAdapterClassGeneratorImpl(logger).apply { setClassFilter(classFilter) }
}