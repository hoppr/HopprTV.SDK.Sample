package com.hoppr.androidsample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var adapter: ViewPagerAdapter
    private var lastTabSelected = 0
    private var maps = mutableMapOf<Int, TabLayout.Tab>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // Set up the ViewPager with an adapter
        adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Link the TabLayout and ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Tab 1"
                1 -> "Tab 2"
                2 -> "Tab 3"
                else -> null
            }
            maps[position] = tab
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                lastTabSelected = position
            }
        })

        viewPager.getChildAt(0).setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                when(lastTabSelected){
                    0 -> findViewById<Button>(R.id.launch_activity_button).requestFocus()
                    1 -> findViewById<Button>(R.id.launch_activity_2_button).requestFocus()
                    2 -> findViewById<Button>(R.id.launch_activity_3_button).requestFocus()
                }
            }
        }
    }

}
