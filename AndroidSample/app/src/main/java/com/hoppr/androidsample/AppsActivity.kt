package com.hoppr.androidsample

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class AppsActivity : AppCompatActivity() {
    private val hopprBundle = Bundle().apply {
        this.putString(HopprParameter.SCREEN_NAME, "AppsScreen")
        this.putString("ParamString", "ParamStringValue")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        // Set up GridLayoutManager
        val gridLayoutManager = GridLayoutManager(this, 3) // 3 columns
        recyclerView.layoutManager = gridLayoutManager

        // Get list of installed apps
        val packageManager = packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        // Define prioritized apps
        val prioritizedApps = listOf(
            "com.netflix.ninja", // Netflix
            "com.disney.disneyplus",  // Disney+
            "com.amazon.amazonvideo.livingroom" // Prime Video
        )

        // Sort apps: prioritized first, then alphabetically
        val sortedApps = apps.sortedWith(Comparator { app1, app2 ->
            val isApp1Prioritized = prioritizedApps.contains(app1.packageName)
            val isApp2Prioritized = prioritizedApps.contains(app2.packageName)

            when {
                isApp1Prioritized && !isApp2Prioritized -> -1
                !isApp1Prioritized && isApp2Prioritized -> 1
                else -> app1.loadLabel(packageManager).toString()
                    .compareTo(app2.loadLabel(packageManager).toString())
            }
        })

        // Set up adapter with sorted list
        recyclerView.adapter = AppAdapter(sortedApps, this)

        // Make RecyclerView focusable for TV remote navigation
        recyclerView.isFocusable = true
        recyclerView.isFocusableInTouchMode = true
        recyclerView.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

        Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, hopprBundle.apply {
            putInt("NbApps", sortedApps.size)
        })
    }

    override fun onDestroy() {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, hopprBundle)
        super.onDestroy()
    }
}

