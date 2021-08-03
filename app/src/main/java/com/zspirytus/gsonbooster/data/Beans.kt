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
    val bar: Bar = Bar()
)

@Boost
data class Bar(
    @SerializedName("bar_int")
    val intValue: Int = 0
)