package com.forjrking.apmfloat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.forjrking.apmlib.ApmOverlayController
import kotlinx.android.synthetic.main.activity_main.*

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repeat(10){
            Thread{
                Thread.sleep(500000)
            }.start()
        }
        text.setOnClickListener {
            startActivity(Intent(this@SecondActivity, ThreeActivity::class.java))
        }
    }
}