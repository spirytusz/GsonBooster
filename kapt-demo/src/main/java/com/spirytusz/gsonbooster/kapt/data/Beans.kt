package com.spirytusz.gsonbooster.kapt.data

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost

@Boost
data class Foo(
    @SerializedName("foo_int")
    val intValue: Int = 0,
    @SerializedName("foo_string")
    val stringValue: String = "",
    @SerializedName("foo_long")
    val longValue: Long = 0L,
    @SerializedName("foo_boolean")
    val booleanValue: Boolean = false,
    @SerializedName("foo_double")
    val doubleValue: Double = 0.0,
    @SerializedName("foo_bar")
    val bar: Bar = Bar(),
    @SerializedName("foo_list_long")
    val listLong: List<Long> = listOf(),
    @SerializedName("foo_list_bar")
    val listBar: List<Bar> = listOf(),
    @SerializedName("foo_set_double")
    val setLong: Set<Double> = setOf(),
    @SerializedName("foo_set_bar")
    val setBar: Set<Bar> = setOf(),
    @SerializedName("foo_list_list_long")
    val nestedList: List<List<Long>> = listOf(),
    @SerializedName("foo_list_set_long")
    val listSet: List<Set<Long>> = listOf(),
    @SerializedName("foo_nullable_bean")
    val nullableBean: NullableBean = NullableBean(),
    @SerializedName("foo_test_enum")
    val testEnum: TestEnum = TestEnum.HELLO,
    @SerializedName("var_bean")
    val varFieldBean: VarFieldBean = VarFieldBean()
)

@Boost
data class Bar(
    @SerializedName("bar_int")
    val intValue: Int = 0,
    @SerializedName("bar_long")
    val longValue: Long = 0L,
    @SerializedName("bar_string")
    val stringValue: String = ""
)

@Boost
data class NullableBean(
    @SerializedName("nullable_int")
    val intValue: Int? = null,
    @SerializedName("nullable_string")
    val stringValue: String? = null,
    @SerializedName("nullable_long")
    val longValue: Long? = null,
    @SerializedName("nullable_boolean")
    val booleanValue: Boolean? = null,
    @SerializedName("nullable_double")
    val doubleValue: Double? = null,
    @SerializedName("nullable_bar")
    val bar: Bar? = null,
    @SerializedName("nullable_list_long")
    val list: List<Long>? = null,
    @SerializedName("nullable_list_bar")
    val listBar: List<Bar>? = null
)

@Boost
data class VarFieldBean(
    @SerializedName("var_int")
    var intValue: Int = 0,
    @SerializedName("var_long")
    var longValue: Long = 0L,
    @SerializedName("val_double")
    val doubleValue: Double = 0.0
) {

    @SerializedName("var_out_constructor_list_long")
    var outConstructorListLongValue: List<Long> = listOf()
}


enum class TestEnum(val intValue: Int) {
    HELLO(1),
    HI(2),
    FINE(3),
    THANKS(4)
}