package com.forjrking.apmfloat

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.forjrking.tools.activity.ActivityManager
import kotlinx.android.synthetic.main.activity_main.*

class ThreeActivity : AppCompatActivity() {
    val list = mutableListOf<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text.setOnClickListener {
//            repeat(10) {
//                val bitmap =
//                    BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher)
//                list.add(bitmap)
//            }

            Log.e("MainActivity",
                ActivityManager.instances.topActivity().toString())
            Log.e("MainActivity",
                ActivityManager.instances.bottomActivity().toString())

        }
    }
}