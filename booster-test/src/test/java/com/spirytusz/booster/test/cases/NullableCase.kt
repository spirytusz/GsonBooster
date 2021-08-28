package com.spirytusz.booster.test.cases

import com.spirytusz.booster.test.cases.base.AbstractCase
import com.spirytusz.booster.test.data.Foo
import com.spirytusz.booster.test.utils.TestUtils
import org.junit.Assert

/**
 * Checking if Generated TypeAdapter can correctly deserialize for nullable fields as Expected.
 */
class NullableCase : AbstractCase<Foo>() {
    override val jsonFileName: String
        get() = "/nullable_case.json"


    override val beanClass: Class<Foo>
        get() = Foo::class.java

    override val expect: Foo?
        get() = TestUtils.makeGson().fromJson(getJson(), beanClass)
}