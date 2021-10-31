package com.spirytusz.gsonbooster

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.spirytusz.gsonbooster.data.Foo

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivityTest"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val json = json()
//        findViewById<Button>(R.id.testBtn).setOnClickListener {
//            val common = GsonBuilder().create()
//            val boost = GsonBuilder()
//                .create()
//
//            val commonTimeCost = traceOnceJson(common, json)
//            val boostTimeCost = traceOnceJson(boost, json)
//            findViewById<TextView>(R.id.result).text = """
//                common time cost: ${TimeUnit.NANOSECONDS.toMicros(commonTimeCost) / 1000.0}
//                boost time cost:  ${TimeUnit.NANOSECONDS.toMicros(boostTimeCost) / 1000.0}
//            """.trimIndent()
//        }
    }

    private fun traceOnceJson(gson: Gson, json: String): Long {
        val start = SystemClock.elapsedRealtimeNanos()
        val bean = kotlin.runCatching {
            gson.fromJson<Foo>(json, Foo::class.java)
        }.onFailure {
            Log.d(TAG, Log.getStackTraceString(it))
        }.getOrNull()
        val end = SystemClock.elapsedRealtimeNanos()
        Log.d(TAG, "$bean")
        return end - start
    }

    private fun json() = assets.open("test.json").bufferedReader().use { it.readText() }
}