package com.spirytusz.booster.test.cases.unexpecttype

import com.spirytusz.booster.test.cases.base.ICase

/**
 * When passing in a type other than expected, check whether the
 * generated TypeAdapter can be deserialized correctly or throw
 * an exception as expected
 *
 * Expect a Int type but receive other type: [IntCases]
 * Expect a Long type but receive other type: [LongCases]
 * Expect a Float type but receive other type: [FloatCases]
 * Expect a Double type but receive other type: [DoubleCases]
 * Expect a String type but receive other type: [StringCases]
 * Expect a Boolean type but receive other type: [BooleanCases]
 * Expect a Object type but receive other type: [ObjectCases]
 * Expect a Array type but receive other type: [ArrayCases]
 */
class UnExpectTypeCase : ICase {
    override fun check() {
        IntCases().check()

        LongCases().check()

        FloatCases().check()

        DoubleCases().check()

        StringCases().check()

        BooleanCases().check()

        ObjectCases().check()

        ArrayCases().check()
    }
}
