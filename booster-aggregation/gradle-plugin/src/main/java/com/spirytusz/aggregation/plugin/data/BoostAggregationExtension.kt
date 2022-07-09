package com.spirytusz.aggregation.plugin.data

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class BoostAggregationExtension @Inject constructor(objectFactory: ObjectFactory) {

    val includeKspGenerated: Property<Boolean> = objectFactory.property(Boolean::class.java).convention(true)

}