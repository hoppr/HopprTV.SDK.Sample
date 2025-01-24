package com.hoppr.androidsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class Activity1 : AppCompatActivity() {
    private val hopprBundle = Bundle().apply {
        this.putString(HopprParameter.SCREEN_NAME, "Activity1Screen")
        this.putString("ParamString", "ParamStringValue")
        this.putStringArray("ParamStringArray", arrayOf("Value 1", "Value 2", "Value 3"))
        this.putIntArray("ParamIntArray", intArrayOf(1, 2, 3))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity1)
        Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, hopprBundle)
    }

    override fun onDestroy() {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, hopprBundle)
        super.onDestroy()
    }
}
