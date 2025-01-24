package com.hoppr.androidsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class Activity2 : AppCompatActivity() {
    private val hopprBundle = Bundle().apply {
        this.putString(HopprParameter.SCREEN_NAME, "Activity2Screen")
        this.putString("ParamString", "ParamStringValue")
        this.putStringArray("ParamStringArray", arrayOf("Value 4", "Value 5", "Value 6"))
        this.putIntArray("ParamIntArray", intArrayOf(4, 5, 6))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity2)
        Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, hopprBundle)
    }


    override fun onDestroy() {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, hopprBundle)
        super.onDestroy()
    }
}
