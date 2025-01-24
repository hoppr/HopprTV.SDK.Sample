package com.hoppr.androidsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class Activity3 : AppCompatActivity() {
    private val hopprBundle = Bundle().apply {
        this.putString(HopprParameter.SCREEN_NAME, "Activity3Screen")
        this.putString("ParamString", "ParamStringValue")
        this.putStringArray("ParamStringArray", arrayOf("Value 7", "Value 8", "Value 9"))
        this.putIntArray("ParamIntArray", intArrayOf(7, 8, 9))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity3)
        Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, hopprBundle)
    }

    override fun onDestroy() {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, hopprBundle)
        super.onDestroy()
    }
}
