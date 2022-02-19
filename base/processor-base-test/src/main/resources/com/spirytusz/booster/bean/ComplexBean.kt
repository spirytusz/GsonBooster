package com.spirytusz.booster.bean

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

@Boost
data class ComplexBean(
    @SerializedName("complex_bean_str")
    var stringValue: String = "",
    @SerializedName("complex_bean_int")
    val intValue: Int = 0,
    @SerializedName("complex_bean_long")
    val longValue: Long = 0L,
    @SerializedName("complex_bean_float")
    val floatValue: Float = 0f,
    @SerializedName("complex_bean_double")
    val doubleValue: Double = 0.0,
    @SerializedName("complex_bean_bool")
    val booleanValue: Boolean = false,
    @SerializedName("complex_bean_interface_long")
    override val interfaceLong: Long = 0L,
    @SerializedName("complex_bean_interface_string")
    override val interfaceString: String = ""
) : SuperBean(), ISuper

open class SuperBean {

    @SerializedName("super_bean_long")
    var superLong: Long = 0L

    @SerializedName("super_bean_str")
    var superString: String = ""
}

interface ISuper {
    val interfaceLong: Long

    val interfaceString: String
}