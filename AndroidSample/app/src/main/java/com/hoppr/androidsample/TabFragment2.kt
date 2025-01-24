package com.hoppr.androidsample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class TabFragment2 : Fragment() {
    private var fragmentView: View? = null

    private val hopprBundle = Bundle().apply {
        this.putString(HopprParameter.SCREEN_NAME, "Tab2")
        this.putInt("TAB_NUMBER", 2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_tab2, container, false)

        val launchActivityButton = fragmentView?.findViewById<Button>(R.id.launch_activity_2_button)
        launchActivityButton?.setOnClickListener {
            onButtonClicked("LaunchActivity2", Activity2::class.java)
        }

        return fragmentView
    }

    private fun onButtonClicked(buttonId: String, intentClass: Class<*>) {
        Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, Bundle().apply {
            putString(HopprParameter.BUTTON_ID, buttonId)
        }) {
            Intent(context, intentClass).apply {
                context?.startActivity(this)
            }
        }
    }

    override fun onResume() {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, hopprBundle)
        super.onResume()
    }

    override fun onPause() {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, hopprBundle)
        super.onPause()
    }
}
