package com.hoppr.jetstream.channel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.tvprovider.media.tv.TvContractCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "InitializeProgramsReceiver"

/**
 * Seeds the JetStream preview channel when the TV launcher asks the app to publish its programs
 * (after install, or after the launcher's data is cleared).
 */
class InitializeProgramsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TvContractCompat.ACTION_INITIALIZE_PROGRAMS) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.i(TAG, LauncherChannelPublisher.initializeProgramsIfNeeded(appContext))
            } catch (e: Exception) {
                Log.e(TAG, "INITIALIZE_PROGRAMS failed", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
