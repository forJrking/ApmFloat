package com.forjrking.apmfloat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.forjrking.apmlib.ApmOverlayController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApmOverlayController.initialize(this,true)
        setContentView(R.layout.activity_main)
    }
}