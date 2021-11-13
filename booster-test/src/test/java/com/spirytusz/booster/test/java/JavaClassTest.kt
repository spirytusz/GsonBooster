package com.spirytusz.booster.test.java

import com.spirytusz.booster.test.base.BaseTest
import com.spirytusz.booster.test.kotlin.KotlinClassTest
import org.junit.Assert
import org.junit.Test

class JavaClassTest: BaseTest() {

    @Test
    fun testPrimitiveValuesJava() {
        val json = json("/primitive_json.json")
        val booster = booster()
        val result = booster.fromJson(json,PrimitiveValuesJava::class.java)
        val expect = PrimitiveValuesJava().apply {
            stringValue = "123"
            intValue = 4
            longValue = 5
            floatValue = 6f
            doubleValue = 7.0
            booleanValue = true
        }
        Assert.assertTrue(expect == result)
    }

    @Test
    fun testCollectionValues() {
        val json = json("/collection_json.json")
        val booster = booster()
        val result = booster.fromJson(json, CollectionValuesJava::class.java)
        val expect = CollectionValuesJava().apply {
            listLong = listOf(1, 2, 3)
            setLong = setOf(4, 5, 6, 6)
            listObject = listOf(
                ObjectValueJava().apply { someMember = "32" },
                ObjectValueJava().apply { someMember = "rba" }
            )
            setObject = setOf(
                ObjectValueJava().apply { someMember = "rgarg" },
                ObjectValueJava().apply { someMember = "rgarg" }
            )
        }
        Assert.assertTrue(expect == result)
    }
}