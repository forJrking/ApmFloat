package com.forjrking.tools

import android.app.Activity

open interface ForegroundCallbacks {
    fun onBecameForeground(currentAct: Activity) {}
    fun onBecameBackground() {}
}