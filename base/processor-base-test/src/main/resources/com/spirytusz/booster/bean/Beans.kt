package com.spirytusz.booster.bean

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

@Boost
data class Bean(
    @SerializedName("bean_str")
    var stringValue: String = "",
    @SerializedName("bean_int")
    val intValue: Int = 0,
    @SerializedName("bean_long")
    val longValue: Long = 0L,
    @SerializedName("bean_float")
    val floatValue: Float = 0f,
    @SerializedName("bean_double")
    val doubleValue: Double = 0.0,
    @SerializedName("bean_bool")
    val booleanValue: Boolean = false,
    @SerializedName("bean_list_str")
    val listString: List<String> = listOf(),
    @SerializedName("bean_java_list_str")
    val javaListString: java.util.ArrayList<String> = arrayListOf(),
    @SerializedName("bean_set_str")
    val setString: Set<String> = setOf(),
    @SerializedName("bean_java_set_str")
    val javaSetString: java.util.HashSet<String> = hashSetOf(),
    @SerializedName("bean_map_str_str")
    val mapStringString: Map<String, String> = mapOf(),
    @SerializedName("bean_java_map_str_str")
    val javaMapStringString: HashMap<String, String> = hashMapOf()
)