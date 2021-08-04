package com.zspirytus.gsonbooster.data

import com.google.gson.annotations.SerializedName
import com.zspirytus.booster.annotation.Boost

@Boost
data class Foo(
    @SerializedName("foo_int")
    val intValue: Int = 0,
    @SerializedName("foo_string")
    val stringValue: String = "",
    @SerializedName("foo_long")
    val longValue: Long = 0L,
    @SerializedName("foo_boolean")
    val booleanValue: Boolean = false,
    @SerializedName("foo_double")
    val doubleValue: Double = 0.0,
    @SerializedName("bar")
    val bar: Bar = Bar(),
    @SerializedName("list")
    val list: List<Long> = listOf(),
    @SerializedName("listBar")
    val listBar: List<Bar> = listOf(),
    @SerializedName("set")
    val set: Set<Double> = setOf(),
    @SerializedName("setBar")
    val setBar: Set<Bar> = setOf(),
    @SerializedName("listList")
    val nestedList: List<List<Long>> = listOf(),
    @SerializedName("listSet")
    val listSet: List<Set<Long>> = listOf()
)

@Boost
data class Bar(
    @SerializedName("bar_int")
    val intValue: Int = 0,
    @SerializedName("bar_long")
    val longValue: Long = 0L,
    @SerializedName("bar_string")
    val stringValue: String = ""
)