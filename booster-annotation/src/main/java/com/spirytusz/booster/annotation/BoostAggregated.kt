package com.spirytusz.booster.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BoostAggregated(val aggregatedResult: String)