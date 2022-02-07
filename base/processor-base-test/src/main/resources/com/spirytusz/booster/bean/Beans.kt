package com.spirytusz.booster.bean

import com.spirytusz.booster.annotation.Boost

open class B {
    var b: String = ""
}

@Boost
data class Bean(
    var string: String = ""
) : B() {
    var a: String = ""
}