package com.forjrking.tools.activity

import android.app.Activity

open interface ForegroundCallbacks {
    fun onBecameForeground(currentAct: Activity)
    fun onBecameBackground()
}