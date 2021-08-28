package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.FloatValueBean
import com.spirytusz.booster.test.utils.TestUtils

class FloatCases : ICase {
    override fun check() {
        Int2FloatCase().check()

        Long2FloatCase().check()

        Double2FloatCase().check()

        String2FloatCase().check()

        Boolean2FloatCase().check()

        Object2FloatCase().check()

        Array2FloatCase().check()
    }
}

abstract class AbstractFloatCase : AbstractCase<FloatValueBean>() {

    override val beanClass: Class<FloatValueBean>
        get() = FloatValueBean::class.java

    override val expect: FloatValueBean?
        get() = TestUtils.makeGson().fromJson(getJson(), beanClass)
}

class Int2FloatCase: AbstractFloatCase() {
    override val jsonFileName: String
        get() = "/float_cases/float_int_case.json"
}

class Long2FloatCase: AbstractFloatCase() {
    override val jsonFileName: String
        get() = "/float_cases/float_long_case.json"
}

class Double2FloatCase: AbstractFloatCase() {
    override val jsonFileName: String
        get() = "/float_cases/float_double_case.json"
}

class String2FloatCase: AbstractFloatCase() {
    override val jsonFileName: String
        get() = "/float_cases/float_string_case.json"

    override val expect: FloatValueBean
        get() = FloatValueBean()
}

class Boolean2FloatCase: AbstractFloatCase() {
    override val jsonFileName: String
        get() = "/float_cases/float_boolean_case.json"

    override val expect: FloatValueBean
        get() = FloatValueBean()
}

class Object2FloatCase: AbstractFloatCase() {
    override val jsonFileName: String
        get() = "/float_cases/float_object_case.json"

    override val expect: FloatValueBean
        get() = FloatValueBean()
}

class Array2FloatCase: AbstractFloatCase() {
    override val jsonFileName: String
        get() = "/float_cases/float_array_case.json"

    override val expect: FloatValueBean
        get() = FloatValueBean()
}