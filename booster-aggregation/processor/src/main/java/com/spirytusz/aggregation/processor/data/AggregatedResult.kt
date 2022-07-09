package com.spirytusz.aggregation.processor.data

import com.google.gson.annotations.SerializedName

data class AggregatedResult(
    @SerializedName("type_adapter_names")
    val typeAdapterNames: Map<String, String> = emptyMap(),
    @SerializedName("type_adapter_factory_names")
    val typeAdapterFactoryNames: Set<String> = emptySet()
) {

    fun isEmpty(): Boolean {
        return typeAdapterNames.isEmpty() && typeAdapterFactoryNames.isEmpty()
    }
}

fun AggregatedResult.merge(that: AggregatedResult): AggregatedResult {
    return AggregatedResult(
        typeAdapterNames = this.typeAdapterNames + that.typeAdapterNames,
        typeAdapterFactoryNames = this.typeAdapterFactoryNames + that.typeAdapterFactoryNames
    )
}

fun List<AggregatedResult>.mergeAggregatedList(): AggregatedResult {
    var result = AggregatedResult()

    forEach {
        result = result.merge(it)
    }

    return result
}