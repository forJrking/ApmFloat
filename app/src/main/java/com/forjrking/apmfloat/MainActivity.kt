package com.forjrking.apmfloat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.forjrking.apmlib.ApmOverlayController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApmOverlayController.initialize(this,true)
        setContentView(R.layout.activity_main)
        text.setOnClickListener {
            startActivity(Intent(this@MainActivity,SecondActivity::class.java))
        }
    }
}