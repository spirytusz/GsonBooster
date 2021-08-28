package com.spirytusz.booster.test.data

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

@Boost
data class Foo(
    @SerializedName("foo_long")
    val longValue: Long = 0L,
    @SerializedName("foo_nullable_long")
    val nullableLong: Long? = 0L,
    @SerializedName("foo_list_bar")
    val listBarTest: List<Bar> = listOf(),
    @SerializedName("foo_nullable_list_bar")
    val nullListBar: List<Bar>? = listOf(),
    @SerializedName("foo_some_class")
    val someClass: SomeClass = SomeClass()
)

@Boost
data class Bar(
    @SerializedName("bar_long")
    val longValue: Long = 0L
) {
    @SerializedName("bar_var_outer_constructor_list_long")
    var outerConstructorListLongValue: List<Long> = listOf()
}

@Boost
class SomeClass {

    @SerializedName("some_class_long")
    var longValue: Long = 0L

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SomeClass

        if (longValue != other.longValue) return false

        return true
    }

    override fun hashCode(): Int {
        return longValue.hashCode()
    }
}

@Boost
data class IntValueBean(
    val key: Int = 0
)

@Boost
data class LongValueBean(
    val key: Long = 0L
)

@Boost
data class FloatValueBean(
    val key: Float = 0f
)

@Boost
data class DoubleValueBean(
    val key: Double = 0.0
)

@Boost
data class StringValueBean(
    val key: String = ""
)

@Boost
data class BooleanValueBean(
    val key: Boolean = false
)

class Test {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

@Boost
data class ObjectValueBean(
    val key: Test = Test()
)

@Boost
data class ArrayValueBean(
    val key: List<Long> = listOf()
)