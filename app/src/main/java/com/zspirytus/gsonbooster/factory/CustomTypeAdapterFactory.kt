package com.zspirytus.gsonbooster.factory

import com.zspirytus.booster.annotation.BoostFactory

@BoostFactory
object CustomTypeAdapterFactory {

    fun get() = BoostCustomTypeAdapterFactory()
}