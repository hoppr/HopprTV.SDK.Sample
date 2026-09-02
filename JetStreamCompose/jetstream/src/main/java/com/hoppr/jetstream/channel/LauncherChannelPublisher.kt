package com.hoppr.jetstream.channel

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.media.tv.TvContract
import android.net.Uri
import android.util.Log
import androidx.tvprovider.media.tv.TvContractCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "LauncherChannelPublisher"
private const val CHANNEL_NAME = "JetStream"

private const val DEEP_LINK_PREFIX = "https://arsmarttv.com/android/portal"

private data class TileInfo(
    val title: String,
    val description: String,
    val imageUrl: String,
    val deeplink: String,
    val weight: Int? = null,
)

private val sampleTiles = listOf(
    TileInfo("Sample Movie 1", "An action-packed adventure", "https://picsum.photos/seed/jstile1/320/180", "$DEEP_LINK_PREFIX/program?id=1", 20),
    TileInfo("Sample Movie 2", "A heartwarming drama",       "https://picsum.photos/seed/jstile2/320/180", "$DEEP_LINK_PREFIX/program?id=2", 30),
    TileInfo("Sample Movie 3", "A thrilling mystery 1",        "https://picsum.photos/seed/jstile3/320/180", "$DEEP_LINK_PREFIX/program?id=3", 40),
    TileInfo("Sample Movie 4", "A thrilling mystery 2",        "https://picsum.photos/seed/jstile3/320/180", "$DEEP_LINK_PREFIX/program?id=4", null),
    TileInfo("Sample Movie 5", "A thrilling mystery 3",        "https://picsum.photos/seed/jstile3/320/180", "$DEEP_LINK_PREFIX/program?id=5", 0),
)

private fun tileIntentUri(deeplink: String): String =
    Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
        .addCategory(Intent.CATEGORY_BROWSABLE)
        .toUri(Intent.URI_INTENT_SCHEME)

/**
 * Publishes the JetStream preview channel to the TV launcher. Shared by the Channel screen's
 * buttons and by [InitializeProgramsReceiver], so both paths produce the same channel and tiles.
 */
object LauncherChannelPublisher {

    /**
     * Seeds the channel only if it is missing or has no programs yet. Safe to call repeatedly.
     */
    suspend fun initializeProgramsIfNeeded(context: Context): String = withContext(Dispatchers.IO) {
        val channelId = findChannelId(context)
        if (channelId != null && hasPrograms(context, channelId)) {
            return@withContext "Channel already populated; nothing to do"
        }
        createLauncherChannel(context)
    }

    suspend fun createLauncherChannel(context: Context): String = withContext(Dispatchers.IO) {
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
                    put(TvContractCompat.PreviewPrograms.COLUMN_INTENT_URI, tileIntentUri(tile.deeplink))
                    tile.weight?.let { weight ->
                        put(TvContractCompat.PreviewPrograms.COLUMN_WEIGHT, weight)
                    }
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

    suspend fun clearPrograms(context: Context): String = withContext(Dispatchers.IO) {
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

    private fun hasPrograms(context: Context, channelId: Long): Boolean {
        val cursor = context.contentResolver.query(
            TvContractCompat.buildPreviewProgramsUriForChannel(channelId),
            arrayOf(TvContractCompat.PreviewPrograms._ID),
            null, null, null
        ) ?: return false

        return cursor.use { it.count > 0 }
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
}
