package com.spirytusz.booster.test.kotlin

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.test.base.BaseTest
import org.junit.Assert
import org.junit.Test

class KotlinClassTest: BaseTest() {

    @Boost
    data class PrimitiveValues(
        @SerializedName("primitive_str")
        val stringValue: String = "",
        @SerializedName("primitive_int")
        val intValue: Int = 0,
        @SerializedName("primitive_long")
        val longValue: Long = 0L,
        @SerializedName("primitive_float")
        val floatValue: Float = 0f,
        @SerializedName("primitive_double")
        val doubleValue: Double = 0.0,
        @SerializedName("primitive_bool")
        val booleanValue: Boolean = false
    )

    @Test
    fun testPrimitiveValues() {
        val json = json("/primitive_json.json")
        val booster = booster()
        val result = booster.fromJson(json, PrimitiveValues::class.java)
        val expect = PrimitiveValues(
            stringValue = "123",
            intValue = 4,
            longValue = 5,
            floatValue = 6f,
            doubleValue = 7.0,
            booleanValue = true
        )
        Assert.assertTrue(expect == result)
    }

    @Boost
    data class CollectionValues(
        @SerializedName("collection_list_long")
        val listLong: List<Long> = emptyList(),
        @SerializedName("collection_set_long")
        val setLong: Set<Long> = emptySet(),
        @SerializedName("collection_list_object")
        val listObject: List<ObjectValue> = emptyList(),
        @SerializedName("collection_set_object")
        val setObject: Set<ObjectValue> = emptySet()
    )

    @Boost
    data class ObjectValue(
        @SerializedName("object_some_member")
        val someMember: String = ""
    )

    @Test
    fun testCollectionValues() {
        val json = json("/collection_json.json")
        val booster = booster()
        val result = booster.fromJson(json, CollectionValues::class.java)
        val expect = CollectionValues(
            listLong = listOf(1, 2, 3),
            setLong = setOf(4, 5, 6, 6),
            listObject = listOf(ObjectValue("32"), ObjectValue("rba")),
            setObject = setOf(ObjectValue("rgarg"), ObjectValue("rgarg"))
        )
        Assert.assertTrue(expect == result)
    }
}