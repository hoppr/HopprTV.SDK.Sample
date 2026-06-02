package com.hoppr.jetstream

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
class DeepLinkActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent?.data?.toString() ?: "<no data>"
        Log.i(TAG, "Partner DeepLinkActivity handled: $url")

        setContentView(
            TextView(this).apply {
                text = "Partner DeepLinkActivity\nhandled:\n$url"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#1B5E20"))
                textSize = 22f
                gravity = Gravity.CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        )
    }

    companion object {
        private const val TAG = "PartnerDeepLink"
    }
}
