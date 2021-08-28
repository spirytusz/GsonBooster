package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.BooleanValueBean

class BooleanCases: ICase {
    override fun check() {
        Int2BooleanCase().check()

        Long2BooleanCase().check()

        Float2BooleanCase().check()

        Double2BooleanCase().check()

        String2BooleanCase().check()

        Object2BooleanCase().check()

        Array2BooleanCase().check()
    }
}

abstract class AbstractBooleanCase: AbstractCase<BooleanValueBean>() {
    override val beanClass: Class<BooleanValueBean>
        get() = BooleanValueBean::class.java

    override val expect: BooleanValueBean
        get() = BooleanValueBean()
}

class Int2BooleanCase: AbstractBooleanCase() {
    override val jsonFileName: String
        get() = "/boolean_cases/boolean_int_case.json"
}

class Long2BooleanCase: AbstractBooleanCase() {
    override val jsonFileName: String
        get() = "/boolean_cases/boolean_long_case.json"
}

class Float2BooleanCase: AbstractBooleanCase() {
    override val jsonFileName: String
        get() = "/boolean_cases/boolean_float_case.json"
}

class Double2BooleanCase: AbstractBooleanCase() {
    override val jsonFileName: String
        get() = "/boolean_cases/boolean_double_case.json"
}

class String2BooleanCase: AbstractBooleanCase() {
    override val jsonFileName: String
        get() = "/boolean_cases/boolean_string_case.json"
}

class Object2BooleanCase: AbstractBooleanCase() {
    override val jsonFileName: String
        get() = "/boolean_cases/boolean_object_case.json"
}

class Array2BooleanCase: AbstractBooleanCase() {
    override val jsonFileName: String
        get() = "/boolean_cases/boolean_array_case.json"
}