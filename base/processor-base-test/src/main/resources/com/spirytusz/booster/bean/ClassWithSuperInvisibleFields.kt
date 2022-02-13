package com.spirytusz.booster.bean

import com.spirytusz.booster.annotation.Boost

@Boost
class ClassWithSuperInvisibleFields : InvisibleFieldClass()

open class InvisibleFieldClass {

    val stringValue: String = ""
}