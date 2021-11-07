package com.spirytusz.booster.processor.config

data class BoosterGenConfig(
    val fromJsonNullSafe: Boolean = false,
    val generateTypeAdapterFactory: Boolean = true,
    val typeAdapterFactoryName: String = "com.spirytusz.booster.BoosterTypeAdapterFactory"
)