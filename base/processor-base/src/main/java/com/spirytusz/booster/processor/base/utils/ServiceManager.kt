package com.spirytusz.booster.processor.base.utils

import java.util.*

object ServiceManager {

    inline fun <reified T> fetchService(): T {
        return ServiceLoader.load(T::class.java, this::class.java.classLoader).iterator().next()
    }
}