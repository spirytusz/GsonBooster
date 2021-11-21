package com.spirytusz.gsonbooster.data

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

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
    @SerializedName("foo_float")
    val floatValue: Float = 0f,
    @SerializedName("foo_list_long")
    val listLong: List<Long> = mutableListOf()
) {

    @Transient
    val ktA: String = ""
}