package com.spirytusz.booster.aggregation.runtime

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object BoosterAggregation {

    val gson: Gson by lazy {
        val builder = GsonBuilder()
        factories.forEach {
            builder.registerTypeAdapterFactory(it)
        }
        builder.create()
    }

    val aggregationTypeAdapters = adapters

    val aggregationTypeAdapterFactories = factories
}