package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.ArrayValueBean

class ArrayCases : ICase {
    override fun check() {
        Int2ArrayCase().check()

        Long2ArrayCase().check()

        Float2ArrayCase().check()

        Double2ArrayCase().check()

        String2ArrayCase().check()

        Boolean2ArrayCase().check()

        Object2ArrayCase().check()
    }
}

abstract class AbstractArrayCase : AbstractCase<ArrayValueBean>() {
    override val beanClass: Class<ArrayValueBean>
        get() = ArrayValueBean::class.java

    override val expect: ArrayValueBean?
        get() = ArrayValueBean()
}

class Int2ArrayCase: AbstractArrayCase() {
    override val jsonFileName: String
        get() = "/array_cases/array_int_case.json"
}

class Long2ArrayCase: AbstractArrayCase() {
    override val jsonFileName: String
        get() = "/array_cases/array_long_case.json"
}

class Float2ArrayCase: AbstractArrayCase() {
    override val jsonFileName: String
        get() = "/array_cases/array_float_case.json"
}

class Double2ArrayCase: AbstractArrayCase() {
    override val jsonFileName: String
        get() = "/array_cases/array_double_case.json"
}

class String2ArrayCase: AbstractArrayCase() {
    override val jsonFileName: String
        get() = "/array_cases/array_string_case.json"
}

class Boolean2ArrayCase: AbstractArrayCase() {
    override val jsonFileName: String
        get() = "/array_cases/array_boolean_case.json"
}

class Object2ArrayCase: AbstractArrayCase() {
    override val jsonFileName: String
        get() = "/array_cases/array_object_case.json"
}