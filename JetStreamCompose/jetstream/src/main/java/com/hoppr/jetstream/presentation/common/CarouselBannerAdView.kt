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

package com.hoppr.jetstream.presentation.common

import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.viewinterop.AndroidView
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.external.banner.BannerAdResult

private const val TAG = "CarouselBannerAdView"

@Composable
fun CarouselBannerAdView(
    adUnit: String = "Banner",
    isCentered: Boolean = false,
    modifier: Modifier = Modifier
) {
    var bannerAdData by remember { mutableStateOf<BannerAdResult.Success?>(null) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var adLoaded by remember { mutableStateOf(false) }
    val currentCentered by rememberUpdatedState(isCentered)

    LaunchedEffect(Unit) {
        Hoppr.requestBannerAd(adUnit) { result ->
            when (result) {
                is BannerAdResult.Success -> {
                    Log.d(TAG, "Banner ad loaded: ${result.bannerAdData.width}x${result.bannerAdData.height}")
                    bannerAdData = result
                }
                is BannerAdResult.Error -> {
                    Log.e(TAG, "Banner ad error: ${result.message}")
                }
            }
        }
    }

    // Subsequent centered changes (the card sliding in/out of center) after the
    // ad has loaded. The initial centered state is delivered by onAdLoaded below.
    LaunchedEffect(isCentered) {
        if (!adLoaded) return@LaunchedEffect
        webView?.evaluateJavascript(
            "window.hopprSetCentered($isCentered);",
            null
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
            webView = null
        }
    }

    bannerAdData?.let { adResult ->
        val banner = adResult.bannerAdData

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        isFocusable = false
                        isFocusableInTouchMode = false
                        addJavascriptInterface(
                            AndroidBridge(
                                onAdLoaded = {
                                    Log.d(TAG, "AndroidBridge.onAdLoaded")
                                    // Bridge thread -> hop to main, then tell the
                                    // creative its current centered state so its
                                    // gated impressions can fire.
                                    post {
                                        adLoaded = true
                                        evaluateJavascript(
                                            "window.hopprSetCentered($currentCentered);",
                                            null
                                        )
                                    }
                                },
                                onAdFailed = { msg ->
                                    Log.e(TAG, "AndroidBridge.onAdFailed: $msg")
                                }
                            ),
                            "AndroidBridge"
                        )
                        loadDataWithBaseURL(
                            banner.baseUrl,
                            banner.content,
                            "text/html",
                            "UTF-8",
                            null
                        )
                        webView = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .focusProperties { canFocus = false }
            )
        }
    }
}
