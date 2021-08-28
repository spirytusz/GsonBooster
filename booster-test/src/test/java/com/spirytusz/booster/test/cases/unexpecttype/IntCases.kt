package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.IntValueBean

class IntCases: ICase {
    override fun check() {
        Long2IntCase().check()

        Float2IntCase().check()

        Double2IntCase().check()

        String2IntCase().check()

        Boolean2IntCase().check()

        Object2IntCase().check()

        Array2IntCase().check()
    }
}

abstract class AbstractIntCase : AbstractCase<IntValueBean>() {
    override val beanClass: Class<IntValueBean>
        get() = IntValueBean::class.java

    override val expect: IntValueBean?
        get() = null
}

class Long2IntCase : AbstractIntCase() {
    override val jsonFileName: String
        get() = "/int_cases/int_long_case.json"
}

class Float2IntCase : AbstractIntCase() {
    override val jsonFileName: String
        get() = "/int_cases/int_float_case.json"
}

class Double2IntCase : AbstractIntCase() {
    override val jsonFileName: String
        get() = "/int_cases/int_double_case.json"
}

class String2IntCase : AbstractIntCase() {
    override val jsonFileName: String
        get() = "/int_cases/int_string_case.json"

    override val expect: IntValueBean
        get() = IntValueBean()
}

class Boolean2IntCase : AbstractIntCase() {
    override val jsonFileName: String
        get() = "/int_cases/int_boolean_case.json"

    override val expect: IntValueBean
        get() = IntValueBean()
}

class Object2IntCase : AbstractIntCase() {
    override val jsonFileName: String
        get() = "/int_cases/int_object_case.json"

    override val expect: IntValueBean
        get() = IntValueBean()
}

class Array2IntCase : AbstractIntCase() {
    override val jsonFileName: String
        get() = "/int_cases/int_array_case.json"

    override val expect: IntValueBean
        get() = IntValueBean()
}