package com.spirytusz.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class Benchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun benchmarkCommon() {
        benchmarkRule.measureRepeated {
            lateinit var gson: Gson
            lateinit var json: String
            lateinit var clazz: Class<*>
            runWithTimingDisabled {
                gson = GsonBuilder().create()
                json = readJson()
                clazz = Foo::class.java
            }
            gson.fromJson(json, clazz)
        }
    }

    @Test
    fun benchmarkBoost() {
        benchmarkRule.measureRepeated {
            lateinit var gson: Gson
            lateinit var json: String
            lateinit var clazz: Class<*>
            runWithTimingDisabled {
                gson = GsonBuilder()
                    .registerTypeAdapterFactory(BoosterTypeAdapterFactory())
                    .create()
                json = readJson()
                clazz = Foo::class.java
            }
            gson.fromJson(json, clazz)
        }
    }

    private fun readJson(): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        return context.assets.open("test.json").bufferedReader().use { it.readText() }
    }
}