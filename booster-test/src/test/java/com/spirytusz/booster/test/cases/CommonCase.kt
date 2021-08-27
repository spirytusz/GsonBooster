package com.spirytusz.booster.test.cases

import com.spirytusz.booster.test.cases.base.ICase
import com.spirytusz.booster.test.data.Foo
import com.spirytusz.booster.test.utils.TestUtils
import org.junit.Assert

class CommonCase : ICase {
    override fun check() {
        val json = TestUtils.getJson("/common_case.json")
        val boost = TestUtils.makeBoostGson()
        val gson = TestUtils.makeGson()
        Assert.assertNotNull(json)

        Assert.assertTrue(
            boost.fromJson(json, Foo::class.java) == gson.fromJson(json, Foo::class.java)
        )
    }
}