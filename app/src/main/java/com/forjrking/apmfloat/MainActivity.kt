package com.forjrking.apmfloat

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.forjrking.apmlib.ApmOverlayController
import com.forjrking.tools.activity.ActivityManager
import com.forjrking.tools.activity.ForegroundCallbacks
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var timer: TimerDown

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApmOverlayController.initialize(this, true)
        setContentView(R.layout.activity_main)

        btn.setOnRecordListener(object : RecordBtn.OnRecordListener {
            override fun takePic() {

            }

            override fun startRecord() {
                timer.start()
            }

            override fun stopRecord() {
                timer.cancel()
            }
        })

        timer = TimerDown(30 * 1000, 1000)

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

    inner class TimerDown(m: Long, l: Long) : CountDownTimer(m, l) {
        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            btn.second = millisUntilFinished / 1000
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}