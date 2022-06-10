package com.spirytusz.booster.processor.gen

import com.google.auto.service.AutoService
import com.spirytusz.booster.processor.base.gen.TypeAdapterFactoryGenerator
import com.spirytusz.booster.processor.base.log.MessageLogger

@AutoService(TypeAdapterFactoryGenerator.Factory::class)
internal class TypeAdapterFactoryGeneratorFactory : TypeAdapterFactoryGenerator.Factory {
    override fun create(logger: MessageLogger): TypeAdapterFactoryGenerator {
        return TypeAdapterFactoryClassGeneratorImpl(logger)
    }
}