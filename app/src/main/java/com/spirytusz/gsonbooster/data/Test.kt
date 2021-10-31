package com.spirytusz.gsonbooster.data

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

@Boost
data class Test(
    @SerializedName(alternate = ["rgag", "rgearherwa"], value = "hahah")
    val a: String
) : BasiConfig() {
    @Transient
    val listLong: List<out Long> = listOf()
}

open class BasiConfig : BasiConfig2(), IConfig, IConfig2 {

    val b: String = ""

    override val d: String
        get() = ""

    override val f: String
        get() = ""
}

open class BasiConfig2 {
    val c: String = ""
}

interface IConfig {
    val d: String
}

interface IConfig2 {
    val f: String
}