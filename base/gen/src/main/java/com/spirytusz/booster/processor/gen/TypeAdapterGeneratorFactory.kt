package com.spirytusz.booster.processor.gen

import com.google.auto.service.AutoService
import com.spirytusz.booster.processor.base.gen.TypeAdapterGenerator
import com.spirytusz.booster.processor.base.log.MessageLogger

@AutoService(TypeAdapterGenerator.Factory::class)
internal class TypeAdapterGeneratorFactory : TypeAdapterGenerator.Factory {

    override fun create(logger: MessageLogger): TypeAdapterGenerator {
        return TypeAdapterGeneratorImpl(logger)
    }
}