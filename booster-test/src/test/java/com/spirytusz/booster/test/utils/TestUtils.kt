package com.spirytusz.booster.test.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spirytusz.booster.BoosterTypeAdapterFactory
import org.junit.Assert

object TestUtils {

    fun getJson(path: String): String? = javaClass.getResource(path)?.readText()

    fun makeBoostGson(): Gson =
        GsonBuilder().registerTypeAdapterFactory(BoosterTypeAdapterFactory()).create()

    fun makeGson(): Gson = GsonBuilder().create()

    inline fun <reified T> compareDeserializeResult(
        gson1: Gson,
        gson2: Gson,
        json: String
    ) {
        Assert.assertTrue(
            gson1.fromJson(json, T::class.java) == gson2.fromJson(
                json,
                T::class.java
            )
        )
    }
}