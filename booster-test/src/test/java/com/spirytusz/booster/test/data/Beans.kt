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