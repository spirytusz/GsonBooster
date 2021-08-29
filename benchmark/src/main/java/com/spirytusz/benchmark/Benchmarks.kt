package com.spirytusz.benchmark

import org.openjdk.jmh.Main
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    Main.main(args)
}

open class Benchmarks {
    companion object {
        private const val FORK = 1
        private const val WARM_UP = 2
        private const val ITERATIONS = 6
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = FORK)
    @Warmup(iterations = WARM_UP)
    @Measurement(iterations = ITERATIONS)
    @OutputTimeUnit(value = TimeUnit.NANOSECONDS)
    fun reflectiveTypeAdapter(gsonState: GsonState, jsonState: JsonState, blackhole: Blackhole) {
        blackhole.consume(gsonState.gson.fromJson(jsonState.json, FooTest::class.java))
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = FORK)
    @Warmup(iterations = WARM_UP)
    @Measurement(iterations = ITERATIONS)
    @OutputTimeUnit(value = TimeUnit.NANOSECONDS)
    fun generatedTypeAdapter(
        boosterState: BoosterState,
        jsonState: JsonState,
        blackhole: Blackhole
    ) {
        blackhole.consume(boosterState.gson.fromJson(jsonState.json, FooTest::class.java))
    }
}