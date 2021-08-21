package com.spirytusz.benchmark

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spirytusz.booster.BoosterTypeAdapterFactory
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import java.util.function.Consumer

@State(Scope.Benchmark)
open class GsonState {

    lateinit var gson: Gson

    @Setup(Level.Trial)
    fun setUp() {
        gson = GsonBuilder().create()
    }
}

@State(Scope.Benchmark)
open class BoosterState {

    lateinit var gson: Gson

    @Setup(Level.Trial)
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapterFactory(BoosterTypeAdapterFactory()).create()
    }
}

@State(Scope.Benchmark)
open class JsonState {

    lateinit var json: String

    @Setup(Level.Trial)
    fun setUp() {
        json = javaClass.getResourceAsStream("/test.json").bufferedReader().use { it.readText() }
    }
}

@State(Scope.Benchmark)
open class BlackHoleState {

    lateinit var blackHole: Consumer<Any>

    @Setup(Level.Trial)
    fun setUp() {
        blackHole = Consumer {  }
    }
}
