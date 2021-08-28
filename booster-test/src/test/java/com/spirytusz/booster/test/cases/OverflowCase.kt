package com.spirytusz.booster.test.cases

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.OverflowFloatValueBean
import com.spirytusz.booster.test.data.OverflowIntValueBean
import com.spirytusz.booster.test.utils.TestUtils

class OverflowCase : ICase {
    override fun check() {
        IntOverflowCase().check()

        FloatOverflowCase().check()
    }
}

class IntOverflowCase : AbstractCase<OverflowIntValueBean>() {
    override val jsonFileName: String
        get() = "/overflow_cases/int_long_cases.json"

    override val beanClass: Class<OverflowIntValueBean>
        get() = OverflowIntValueBean::class.java

    override val expect: OverflowIntValueBean?
        get() = null
}

class FloatOverflowCase : AbstractCase<OverflowFloatValueBean>() {
    override val jsonFileName: String
        get() = "/overflow_cases/int_long_cases.json"

    override val beanClass: Class<OverflowFloatValueBean>
        get() = OverflowFloatValueBean::class.java

    override val expect: OverflowFloatValueBean
        get() = TestUtils.makeGson().fromJson(getJson(), beanClass)
}