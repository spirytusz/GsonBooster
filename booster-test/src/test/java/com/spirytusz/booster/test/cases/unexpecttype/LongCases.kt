package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.LongValueBean
import com.spirytusz.booster.test.utils.TestUtils

class LongCases: ICase {
    override fun check() {
        Int2LongCase().check()

        Float2LongCase().check()

        Double2LongCase().check()

        String2LongCase().check()

        Boolean2LongCase().check()

        Object2LongCase().check()

        Array2LongCase().check()
    }
}

abstract class AbstractLongCase: AbstractCase<LongValueBean>() {

    override val beanClass: Class<LongValueBean>
        get() = LongValueBean::class.java

    override val expect: LongValueBean?
        get() = TestUtils.makeGson().fromJson(getJson(), beanClass)
}

class Int2LongCase : AbstractLongCase() {

    override val jsonFileName: String
        get() = "/long_cases/long_int_case.json"
}

class Float2LongCase : AbstractLongCase() {

    override val jsonFileName: String
        get() = "/long_cases/long_float_case.json"

    override val expect: LongValueBean?
        get() = null
}

class Double2LongCase : AbstractLongCase() {

    override val jsonFileName: String
        get() = "/long_cases/long_double_case.json"

    override val expect: LongValueBean?
        get() = null
}

class String2LongCase : AbstractLongCase() {

    override val jsonFileName: String
        get() = "/long_cases/long_string_case.json"

    override val expect: LongValueBean
        get() = LongValueBean()
}

class Boolean2LongCase : AbstractLongCase() {

    override val jsonFileName: String
        get() = "/long_cases/long_boolean_case.json"

    override val expect: LongValueBean
        get() = LongValueBean()
}

class Object2LongCase : AbstractLongCase() {

    override val jsonFileName: String
        get() = "/long_cases/long_object_case.json"

    override val expect: LongValueBean
        get() = LongValueBean()
}

class Array2LongCase : AbstractLongCase() {

    override val jsonFileName: String
        get() = "/long_cases/long_array_case.json"

    override val expect: LongValueBean
        get() = LongValueBean()
}