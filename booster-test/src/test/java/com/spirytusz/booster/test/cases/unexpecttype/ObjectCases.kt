package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.ObjectValueBean

class ObjectCases : ICase {
    override fun check() {
        Int2ObjectCase().check()

        Long2ObjectCase().check()

        Float2ObjectCase().check()

        Double2ObjectCase().check()

        String2ObjectCase().check()

        Boolean2ObjectCase().check()

        Array2ObjectCase().check()
    }
}

abstract class AbstractObjectCase : AbstractCase<ObjectValueBean>() {
    override val beanClass: Class<ObjectValueBean>
        get() = ObjectValueBean::class.java

    override val expect: ObjectValueBean?
        get() = ObjectValueBean()
}

class Int2ObjectCase: AbstractObjectCase() {
    override val jsonFileName: String
        get() = "/object_cases/object_int_case.json"
}

class Long2ObjectCase: AbstractObjectCase() {
    override val jsonFileName: String
        get() = "/object_cases/object_long_case.json"
}

class Float2ObjectCase: AbstractObjectCase() {
    override val jsonFileName: String
        get() = "/object_cases/object_float_case.json"
}

class Double2ObjectCase: AbstractObjectCase() {
    override val jsonFileName: String
        get() = "/object_cases/object_double_case.json"
}

class String2ObjectCase: AbstractObjectCase() {
    override val jsonFileName: String
        get() = "/object_cases/object_string_case.json"
}

class Boolean2ObjectCase: AbstractObjectCase() {
    override val jsonFileName: String
        get() = "/object_cases/object_boolean_case.json"
}

class Array2ObjectCase: AbstractObjectCase() {
    override val jsonFileName: String
        get() = "/object_cases/object_int_case.json"
}