package com.spirytusz.booster.test

import com.spirytusz.booster.test.cases.CommonCase
import com.spirytusz.booster.test.cases.InvalidJsonCase
import com.spirytusz.booster.test.cases.NullableCase
import com.spirytusz.booster.test.cases.OverflowCase
import com.spirytusz.booster.test.cases.unexpecttype.UnExpectTypeCase
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

    @Test
    fun testUnExpectType() {
        UnExpectTypeCase().check()
    }

    @Test
    fun testOverflow() {
        OverflowCase().check()
    }

    @Test
    fun testInvalidJson() {
        InvalidJsonCase().check()
    }
}