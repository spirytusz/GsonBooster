package com.zspirytus.gsonbooster

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zspirytus.gsonbooster.data.Foo
import com.zspirytus.gsonbooster.data.FooTypeAdapter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.testBtn).setOnClickListener {
            val common = GsonBuilder().create()
            val boost = GsonBuilder()
                .registerTypeAdapter(Foo::class.java, FooTypeAdapter())
                .create()

            val json = json()
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
        gson.fromJson<Foo>(json, Foo::class.java)
        val end = SystemClock.elapsedRealtimeNanos()
        return end - start
    }

    private fun json() = assets.open("test.json").bufferedReader().use { it.readText() }
}