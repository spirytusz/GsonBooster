package com.spirytusz.booster.test.cases

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.data.Foo
import com.spirytusz.booster.test.utils.TestUtils

/**
 * Checking if Generated TypeAdapter can correctly deserialize as Expected.
 */
class CommonCase : AbstractCase<Foo>() {

    override val jsonFileName: String
        get() = "/common_case.json"


    override val beanClass: Class<Foo>
        get() = Foo::class.java

    override val expect: Foo?
        get() = TestUtils.makeGson().fromJson(getJson(), beanClass)
}