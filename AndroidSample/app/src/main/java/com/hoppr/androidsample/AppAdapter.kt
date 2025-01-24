package com.hoppr.androidsample

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

class AppAdapter(
    private val apps: List<ApplicationInfo>,
    private val context: Context
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        val packageManager = context.packageManager

        holder.appName.text = app.loadLabel(packageManager).toString()
        holder.appIcon.setImageDrawable(app.loadIcon(packageManager))

        holder.itemView.setOnClickListener {
            Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, Bundle().apply {
                this.putString(HopprParameter.BUTTON_ID, "LaunchExternalApp")
                this.putString("appName", app.packageName)
            }){
                launchApp(packageManager, app)
            }
        }

        // Ensure focus is visible
        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.elevation = 10f // Add slight elevation to indicate focus
            } else {
                view.elevation = 0f
            }
        }
    }
    private fun launchApp(packageManager: PackageManager, app: ApplicationInfo){
        val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(context, "Unable to launch ${app.loadLabel(packageManager)}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = apps.size

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.app_name)
        val appIcon: ImageView = view.findViewById(R.id.app_icon)
    }
}
