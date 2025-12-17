/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hoppr.jetstream.presentation.screens.video

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.tv.material3.Text
import com.hoppr.jetstream.ObserveHopprScreen
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.external.video.VideoResult
import com.hoppr.jetstream.presentation.screens.videoPlayer.components.rememberPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "VideoScreen"

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean
) {
    ObserveHopprScreen(Bundle().apply {
        putString(HopprParameter.SCREEN_NAME, "video")
    })

    var videoUrl by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPlayerVisible by remember { mutableStateOf(true) }
    val exoPlayer = rememberPlayer(LocalContext.current)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            when (val result = Hoppr.requestVideoAd("Video")) {
                is VideoResult.Success -> {
                    Log.d(TAG, "Video ad loaded successfully!")
                    Log.d(TAG, "VAST XML length: ${result.content.length} characters")

                    // Parse VAST XML to extract video URL
                    val url = parseVastXml(result.content)
                    withContext(Dispatchers.Main) {
                        if (url != null) {
                            videoUrl = url
                            Log.d(TAG, "Extracted video URL: $url")
                        } else {
                            errorMessage = "Failed to parse video URL from VAST"
                            Log.e(TAG, "Failed to parse video URL from VAST")
                        }
                    }
                }
                is VideoResult.Error -> {
                    Log.e(TAG, "Failed to load video ad: ${result.message}")
                    withContext(Dispatchers.Main) {
                        errorMessage = result.message
                    }
                }
            }

            when (val result = Hoppr.requestVideoTag("Video")) {
                is VideoResult.Success -> {
                    Log.d(TAG, "Video tag URL retrieved successfully!")
                    Log.d(TAG, "Tag URL: ${result.content}")
                    // You can now use result.content to fetch VAST XML yourself or pass to a video player
                }
                is VideoResult.Error -> {
                    Log.e(TAG, "Failed to get video tag: ${result.message}")
                }
            }
        }
    }

    LaunchedEffect(videoUrl) {
        videoUrl?.let { url ->
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.repeatMode = androidx.media3.common.Player.REPEAT_MODE_OFF
            exoPlayer.prepare()
            exoPlayer.play()

            // Add listener to detect when video ends
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        Log.d(TAG, "Video playback ended")
                        isPlayerVisible = false
                        exoPlayer.release()
                    }
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            errorMessage != null -> {
                Text("Error: $errorMessage")
            }
            videoUrl != null && isPlayerVisible -> {
                PlayerSurface(
                    player = exoPlayer,
                    surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
                    modifier = Modifier
                        .resizeWithContentScale(
                            contentScale = ContentScale.Fit,
                            sourceSizeDp = null
                        )
                )
            }
            !isPlayerVisible -> {
                Text("Video playback completed")
            }
            else -> {
                Text("Loading video ad...")
            }
        }
    }
}

private fun parseVastXml(vastXml: String): String? {
    return try {
        // Simple regex to extract MediaFile URL from VAST XML
        val mediaFileRegex = "<MediaFile[^>]*>\\s*<!\\[CDATA\\[([^\\]]+)\\]\\]>\\s*</MediaFile>".toRegex()
        val match = mediaFileRegex.find(vastXml)
        match?.groupValues?.get(1)?.trim()
            ?: run {
                // Try without CDATA
                val simpleRegex = "<MediaFile[^>]*>\\s*([^<]+)\\s*</MediaFile>".toRegex()
                simpleRegex.find(vastXml)?.groupValues?.get(1)?.trim()
            }
    } catch (e: Exception) {
        Log.e(TAG, "Error parsing VAST XML", e)
        null
    }
}
