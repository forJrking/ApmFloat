package com.forjrking.apmfloat

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.forjrking.apmlib.ApmOverlayController
import com.forjrking.tools.activity.ActivityManager
import com.forjrking.tools.activity.ForegroundCallbacks
import com.forjrking.tools.time.TimerListener
import com.forjrking.tools.time.toCountTime
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApmOverlayController.initialize(this, true)
        setContentView(R.layout.activity_main)
        val countDownTimer = com.forjrking.tools.time.CountDownTimer(20000, 1000)

        countDownTimer.setCountDownListener(object : TimerListener {
            override fun onFinish() {
                text.text = "onFinish"
            }

            override fun onTick(millisUntilFinished: Long) {
                text.text = millisUntilFinished.toCountTime(true)
            }

        })
        btn.setOnRecordListener(object : RecordBtn.OnRecordListener {
            override fun takePic() {

            }

            override fun startRecord() {
                countDownTimer.start()
            }

            override fun stopRecord() {
                countDownTimer.cancel()
            }
        })



        text.setOnClickListener {

//            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
        ActivityManager.instances.initialize(this.application)

        ActivityManager.instances.addListener(object :
            ForegroundCallbacks {

            override fun onBecameForeground(currentAct: Activity) {
                Log.e("MainActivity", "onBecameForeground")
            }

            override fun onBecameBackground() {
                Log.e("MainActivity", "onBecameBackground")
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}