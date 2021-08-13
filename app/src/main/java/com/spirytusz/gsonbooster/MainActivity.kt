package com.spirytusz.gsonbooster

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spirytusz.gsonbooster.data.Foo
import com.spirytusz.gsonbooster.factory.CustomTypeAdapterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivityTest"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val json = json()
        findViewById<Button>(R.id.testBtn).setOnClickListener {
            val common = GsonBuilder().create()
            val boost = GsonBuilder()
                .registerTypeAdapterFactory(CustomTypeAdapterFactory.get())
                .create()

            val commonTimeCost = traceOnceJson(common, json)
            val boostTimeCost = traceOnceJson(boost, json)
            findViewById<TextView>(R.id.result).text = """
                common time cost: ${TimeUnit.NANOSECONDS.toMicros(commonTimeCost) / 1000.0}
                boost time cost:  ${TimeUnit.NANOSECONDS.toMicros(boostTimeCost) / 1000.0}
            """.trimIndent()
        }
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