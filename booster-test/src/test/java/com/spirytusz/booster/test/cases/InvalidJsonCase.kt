package com.spirytusz.booster.test.cases

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.InvalidJsonTestBean

class InvalidJsonCase : ICase {
    override fun check() {
        EmptyJsonCase().check()

        NoEndingObjectCase().check()

        InvalidNumberCase().check()
    }
}

class EmptyJsonCase : AbstractCase<InvalidJsonTestBean>() {
    override val jsonFileName: String
        get() = "/invalid_json_cases/invalid_json_empty_string.json"

    override val beanClass: Class<InvalidJsonTestBean>
        get() = InvalidJsonTestBean::class.java

    override val expect: InvalidJsonTestBean?
        get() = null
}

class NoEndingObjectCase: AbstractCase<InvalidJsonTestBean>() {
    override val jsonFileName: String
        get() = "/invalid_json_cases/invalid_json_no_ending_object.json"

    override val beanClass: Class<InvalidJsonTestBean>
        get() = InvalidJsonTestBean::class.java

    override val expect: InvalidJsonTestBean?
        get() = null
}

class InvalidNumberCase: AbstractCase<InvalidJsonTestBean>() {
    override val jsonFileName: String
        get() = "/invalid_json_cases/invalid_json_invalid_number.json"

    override val beanClass: Class<InvalidJsonTestBean>
        get() = InvalidJsonTestBean::class.java

    override val expect: InvalidJsonTestBean
        get() = InvalidJsonTestBean()
}