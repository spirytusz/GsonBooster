package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.StringValueBean

class StringCases : ICase {
    override fun check() {
        Int2StringCase().check()

        Long2StringCase().check()

        Float2StringCase().check()

        Double2StringCase().check()

        Boolean2StringCase().check()

        Object2StringCase().check()

        Array2StringCase().check()
    }
}

abstract class AbstractStringCase : AbstractCase<StringValueBean>() {
    override val beanClass: Class<StringValueBean>
        get() = StringValueBean::class.java

    override val expect: StringValueBean
        get() = StringValueBean()
}

class Int2StringCase : AbstractStringCase() {
    override val jsonFileName: String
        get() = "/string_cases/string_int_case.json"
}

class Long2StringCase : AbstractStringCase() {
    override val jsonFileName: String
        get() = "/string_cases/string_long_case.json"
}

class Float2StringCase : AbstractStringCase() {
    override val jsonFileName: String
        get() = "/string_cases/string_float_case.json"
}

class Double2StringCase : AbstractStringCase() {
    override val jsonFileName: String
        get() = "/string_cases/string_double_case.json"
}

class Boolean2StringCase : AbstractStringCase() {
    override val jsonFileName: String
        get() = "/string_cases/string_boolean_case.json"
}

class Object2StringCase : AbstractStringCase() {
    override val jsonFileName: String
        get() = "/string_cases/string_object_case.json"
}

class Array2StringCase : AbstractStringCase() {
    override val jsonFileName: String
        get() = "/string_cases/string_array_case.json"
}