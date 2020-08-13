package com.forjrking.apmfloat

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.forjrking.tools.activity.ActivityManager
import com.forjrking.tools.time.CountDownTimer
import com.forjrking.tools.time.TimerListener
import com.forjrking.tools.time.toCountTime
import kotlinx.android.synthetic.main.activity_main.*

class ThreeActivity : AppCompatActivity() {
    val list = mutableListOf<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val countDownTimer = CountDownTimer(3000, 1000)
        text.setOnClickListener {
            if(!countDownTimer.isRunning) {
                countDownTimer.start()
            }else{
                countDownTimer.cancel()
            }
//            repeat(10) {
//                val bitmap =
//                    BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher)
//                list.add(bitmap)
//            }

//            Log.e(
//                "MainActivity",
//                ActivityManager.instances.topActivity().toString()
//            )
//            Log.e(
//                "MainActivity",
//                ActivityManager.instances.bottomActivity().toString()
//            )

        }

        countDownTimer.setCountDownListener(object : TimerListener {
            override fun onFinish() {
                text.text = "onFinish"
            }

            override fun onTick(millisUntilFinished: Long) {
                text.text = millisUntilFinished.toCountTime()
            }

        })
    }

    override fun onResume() {
        super.onResume()
    }
}