package com.spirytusz.booster.bean

import com.spirytusz.booster.annotation.Boost

open class B {
    var b: String = ""
}

@Boost
data class Bean(
    var string: String = "",
    val intValue: Int = 0
) : B() {
    var a: String = ""
    val c: Long = 0L
}