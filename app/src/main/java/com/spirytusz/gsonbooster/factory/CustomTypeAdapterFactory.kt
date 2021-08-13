package com.spirytusz.gsonbooster.factory

import com.spirytusz.booster.annotation.BoostFactory

@BoostFactory
object CustomTypeAdapterFactory {

    fun get() = BoostCustomTypeAdapterFactory()
}