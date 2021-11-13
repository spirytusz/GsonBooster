package com.spirytusz.booster.test.base

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spirytusz.booster.test.BoostTestTypeAdapterFactory

open class BaseTest {

    protected fun json(path: String): String = javaClass.getResource(path)!!.readText()

    protected fun booster(): Gson =
        GsonBuilder().registerTypeAdapterFactory(BoostTestTypeAdapterFactory()).create()
}