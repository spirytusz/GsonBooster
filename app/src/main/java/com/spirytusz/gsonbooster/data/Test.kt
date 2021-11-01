package com.spirytusz.gsonbooster.data

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

@Boost
data class Test(
    @SerializedName(alternate = ["rgag", "rgearherwa"], value = "hahah")
    val a: String = "",
    val b: String = "",
    val beanJava: BeansJava = BeansJava(),
    @SerializedName("strategy")
    val strategy: Strategy = Strategy.HIGH,
    override val d: String = "",
    override val f: String = ""
) : BasiConfig() {
    @Transient
    val listLong: List<out Long> = listOf()
}

enum class Strategy {
    LOW,
    MEDIUM,
    HIGH
}

abstract class BasiConfig : BasiConfig2(), IConfig, IConfig2 {
}

open class BasiConfig2 {

}

interface IConfig {
    val d: String
}

interface IConfig2 {
    val f: String
}