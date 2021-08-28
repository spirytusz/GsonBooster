package com.spirytusz.booster.test.cases.base

import com.spirytusz.booster.test.utils.TestUtils
import org.junit.Assert

abstract class AbstractCase<T> : ICase {

    abstract val jsonFileName: String

    abstract val beanClass: Class<T>

    abstract val expect: T?

    override fun check() {
        val json = getJson()
        val boost = TestUtils.makeBoostGson()
        val actual = runCatching {
            boost.fromJson(json, beanClass)
        }.getOrNull()
        Assert.assertTrue(expect == actual)
    }

    protected fun getJson() = TestUtils.getJson(jsonFileName)!!
}