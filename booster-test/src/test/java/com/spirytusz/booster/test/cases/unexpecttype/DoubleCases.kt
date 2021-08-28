package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.DoubleValueBean
import com.spirytusz.booster.test.utils.TestUtils

class DoubleCases : ICase {
    override fun check() {
        Int2DoubleCase().check()

        Long2FDoubleCase().check()

        Float2DoubleCase().check()

        String2DoubleCase().check()

        Boolean2DoubleCase().check()

        Object2DoubleCase().check()

        Array2DoubleCase().check()
    }
}

abstract class AbstractDoubleCase : AbstractCase<DoubleValueBean>() {
    override val beanClass: Class<DoubleValueBean>
        get() = DoubleValueBean::class.java
    override val expect: DoubleValueBean?
        get() = TestUtils.makeGson().fromJson(getJson(), beanClass)
}

class Int2DoubleCase : AbstractDoubleCase() {
    override val jsonFileName: String
        get() = "/double_cases/double_int_case.json"
}

class Long2FDoubleCase : AbstractDoubleCase() {
    override val jsonFileName: String
        get() = "/double_cases/double_long_case.json"
}

class Float2DoubleCase : AbstractDoubleCase() {
    override val jsonFileName: String
        get() = "/double_cases/double_float_case.json"
}

class String2DoubleCase : AbstractDoubleCase() {
    override val jsonFileName: String
        get() = "/double_cases/double_string_case.json"

    override val expect: DoubleValueBean
        get() = DoubleValueBean()
}

class Boolean2DoubleCase : AbstractDoubleCase() {
    override val jsonFileName: String
        get() = "/double_cases/double_boolean_case.json"

    override val expect: DoubleValueBean
        get() = DoubleValueBean()
}

class Object2DoubleCase : AbstractDoubleCase() {
    override val jsonFileName: String
        get() = "/double_cases/double_object_case.json"

    override val expect: DoubleValueBean
        get() = DoubleValueBean()
}

class Array2DoubleCase : AbstractDoubleCase() {
    override val jsonFileName: String
        get() = "/double_cases/double_array_case.json"

    override val expect: DoubleValueBean
        get() = DoubleValueBean()
}