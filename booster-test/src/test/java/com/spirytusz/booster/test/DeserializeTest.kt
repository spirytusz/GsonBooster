package com.spirytusz.booster.test

import com.spirytusz.booster.test.cases.CommonCase
import com.spirytusz.booster.test.cases.NullableCase
import org.junit.Test

class DeserializeTest {

    @Test
    fun testCommon() {
        CommonCase().check()
    }

    @Test
    fun testNullable() {
        NullableCase().check()
    }
}