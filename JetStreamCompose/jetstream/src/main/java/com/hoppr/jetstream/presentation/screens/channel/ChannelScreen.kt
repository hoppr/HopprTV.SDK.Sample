package com.hoppr.jetstream.presentation.screens.channel

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.media.tv.TvContract
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import androidx.tvprovider.media.tv.TvContractCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ChannelScreen"
private const val CHANNEL_NAME = "JetStream"

private data class TileInfo(val title: String, val description: String, val imageUrl: String)

private val sampleTiles = listOf(
    TileInfo("Sample Movie 1", "An action-packed adventure", "https://picsum.photos/seed/jstile1/320/180"),
    TileInfo("Sample Movie 2", "A heartwarming drama",       "https://picsum.photos/seed/jstile2/320/180"),
    TileInfo("Sample Movie 3", "A thrilling mystery",        "https://picsum.photos/seed/jstile3/320/180")
)

@Composable
fun ChannelScreen(
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        scope.launch {
                            status = createLauncherChannel(context)
                            isLoading = false
                        }
                    }
                }
            ) {
                Text(if (isLoading) "Creating..." else "Create Channel")
            }

            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        scope.launch {
                            status = clearPrograms(context)
                            isLoading = false
                        }
                    }
                }
            ) {
                Text("Clear Programs")
            }

            status?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it)
            }
        }
    }
}

private suspend fun createLauncherChannel(context: Context): String = withContext(Dispatchers.IO) {
    try {
        val appLinkUri = Intent(Intent.ACTION_MAIN)
            .apply { setPackage(context.packageName) }
            .toUri(Intent.URI_INTENT_SCHEME)

        val channelId = findChannelId(context) ?: run {
            val channelValues = ContentValues().apply {
                put(TvContractCompat.Channels.COLUMN_TYPE, TvContractCompat.Channels.TYPE_PREVIEW)
                put(TvContractCompat.Channels.COLUMN_DISPLAY_NAME, CHANNEL_NAME)
                put(TvContractCompat.Channels.COLUMN_DESCRIPTION, "Top picks from JetStream")
                put(TvContractCompat.Channels.COLUMN_APP_LINK_INTENT_URI, appLinkUri)
            }
            val channelUri = context.contentResolver.insert(
                TvContractCompat.Channels.CONTENT_URI, channelValues
            ) ?: return@withContext "Error: failed to create channel"

            channelUri.lastPathSegment?.toLong()
                ?: return@withContext "Error: invalid channel ID"
        }

        sampleTiles.forEach { tile ->
            val programValues = ContentValues().apply {
                put(TvContractCompat.PreviewPrograms.COLUMN_CHANNEL_ID, channelId)
                put(TvContractCompat.PreviewPrograms.COLUMN_TITLE, tile.title)
                put(TvContractCompat.PreviewPrograms.COLUMN_SHORT_DESCRIPTION, tile.description)
                put(TvContractCompat.PreviewPrograms.COLUMN_POSTER_ART_URI, tile.imageUrl)
                put(TvContractCompat.PreviewPrograms.COLUMN_TYPE, TvContractCompat.PreviewPrograms.TYPE_MOVIE)
                put(TvContractCompat.PreviewPrograms.COLUMN_INTENT_URI, appLinkUri)
            }
            context.contentResolver.insert(TvContractCompat.PreviewPrograms.CONTENT_URI, programValues)
        }

        notifyLauncherUpdate(context)
        safelyRequestChannelPin(context, channelId)

        "Channel created with ${sampleTiles.size} tiles!"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

private suspend fun clearPrograms(context: Context): String = withContext(Dispatchers.IO) {
    try {
        val cursor = context.contentResolver.query(
            TvContractCompat.PreviewPrograms.CONTENT_URI,
            arrayOf(TvContractCompat.PreviewPrograms._ID),
            null, null, null
        ) ?: return@withContext "Error: could not query programs"

        var count = 0
        cursor.use {
            while (it.moveToNext()) {
                val programId = it.getLong(0)
                context.contentResolver.delete(
                    TvContractCompat.buildPreviewProgramUri(programId),
                    null, null
                )
                count++
            }
        }

        notifyLauncherUpdate(context)
        "$count program(s) cleared"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

private fun findChannelId(context: Context): Long? {
    val cursor = context.contentResolver.query(
        TvContractCompat.Channels.CONTENT_URI,
        arrayOf(TvContractCompat.Channels._ID, TvContractCompat.Channels.COLUMN_DISPLAY_NAME),
        null, null, null
    ) ?: return null

    return cursor.use {
        while (it.moveToNext()) {
            val id = it.getLong(0)
            val name = it.getString(1)
            if (name == CHANNEL_NAME) return@use id
        }
        null
    }
}

private fun notifyLauncherUpdate(context: Context) {
    try {
        val intent = Intent("com.google.android.tvlauncher.action.CHANNELS_UPDATED").apply {
            setPackage("com.google.android.tvlauncher")
        }
        context.sendBroadcast(intent)
    } catch (e: Exception) {
        Log.w(TAG, "Launcher update broadcast failed: ${e.message}")
    }
}

private fun safelyRequestChannelPin(context: Context, channelId: Long) {
    if (isSystemApp(context)) return

    try {
        TvContract.requestChannelBrowsable(context, channelId)
    } catch (e: Exception) {
        Log.d(TAG, "Launcher pin request skipped: ${e.message}")
    }
}

private fun isSystemApp(context: Context): Boolean {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    } catch (e: Exception) {
        false
    }
}
