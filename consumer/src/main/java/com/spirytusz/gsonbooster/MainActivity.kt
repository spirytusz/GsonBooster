package com.spirytusz.gsonbooster

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.spirytusz.gsonbooster.data.Foo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivityTest"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testBtn.setOnClickListener {
            val json = json()
            val booster = GsonBuilder()
                .registerTypeAdapterFactory(ExampleTypeAdapterFactory())
                .create()
            val result = booster.fromJson(json, Foo::class.java)
            Log.i(TAG, "result=$result")
        }
    }

    private fun json() = assets.open("test.json").bufferedReader().use { it.readText() }
}