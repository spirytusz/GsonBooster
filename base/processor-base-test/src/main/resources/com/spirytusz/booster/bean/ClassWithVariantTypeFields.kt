package com.spirytusz.booster.bean

import com.spirytusz.booster.annotation.Boost

@Boost
class ClassWithVariantTypeFields {

    var listOutString: List<out String> = listOf()
}