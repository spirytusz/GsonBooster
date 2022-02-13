package com.spirytusz.booster.bean

import com.spirytusz.booster.annotation.Boost

@Boost
class ClassWithGenerics<T> {

    var stringValue: String = ""
}